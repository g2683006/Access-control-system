// 管理页面通用的分页、增删改逻辑。页面里提供 api（接口前缀）、idKey（主键名）、searchKey（搜索字段）和 emptyForm()
export default {
    data() {
        return {
            list: [],
            total: 0,
            pageNum: 1,
            pageSize: 10,
            keyword: '',
            loading: false,
            selection: [],
            dialogVisible: false,
            form: {}
        }
    },
    created() {
        this.load(1)
    },
    methods: {
        pageParams(pageNum) {
            return {pageNum, pageSize: this.pageSize, [this.searchKey]: this.keyword}
        },
        load(pageNum) {
            this.pageNum = pageNum || this.pageNum
            this.loading = true
            this.request.get(this.api + '/page', {params: this.pageParams(this.pageNum)}).then(res => {
                if (res.code === 200) {
                    this.list = res.data.records
                    this.total = res.data.total
                    // 删光了最后一页时回到上一页
                    if (!this.list.length && this.pageNum > 1) {
                        this.load(this.pageNum - 1)
                    }
                } else {
                    this.$message.error(res.msg)
                }
            }).finally(() => {
                this.loading = false
            })
        },
        add() {
            this.form = this.emptyForm()
            this.openDialog()
        },
        edit(row) {
            this.form = {...row}
            this.openDialog()
        },
        openDialog() {
            this.dialogVisible = true
            this.$nextTick(() => this.$refs.form && this.$refs.form.clearValidate())
        },
        saveUrl() {
            return this.api + '/save'
        },
        save() {
            this.$refs.form.validate(valid => {
                if (!valid) {
                    return
                }
                this.request.post(this.saveUrl(this.form), this.form).then(res => {
                    if (res.code === 200) {
                        this.$message.success('保存成功')
                        this.dialogVisible = false
                        this.load()
                    } else {
                        this.$message.error(res.msg)
                    }
                })
            })
        },
        remove(row) {
            this.$confirm('确定删除吗？', '提示', {type: 'warning'}).then(() => {
                this.request.delete(this.api + '/delete/' + row[this.idKey]).then(this.afterRemove)
            }).catch(() => {
            })
        },
        removeBatch() {
            if (!this.selection.length) {
                this.$message.warning('请先勾选要删除的数据')
                return
            }
            this.$confirm(`确定删除选中的 ${this.selection.length} 条数据吗？`, '提示', {type: 'warning'}).then(() => {
                this.request.post(this.api + '/delete/batch', this.selection.map(row => row[this.idKey])).then(this.afterRemove)
            }).catch(() => {
            })
        },
        afterRemove(res) {
            if (res.code === 200) {
                this.$message.success('删除成功')
                this.load()
            } else {
                this.$message.error(res.msg)
            }
        }
    }
}
