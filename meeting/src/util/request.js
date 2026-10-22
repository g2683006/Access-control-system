import axios from 'axios'
import ElementUI from 'element-ui'

const request = axios.create({
    // 后端地址，默认 http://localhost:8888，可以在 .env.local 里用 VUE_APP_API_BASE 改
    baseURL: process.env.VUE_APP_API_BASE || 'http://localhost:8888',
    timeout: 15000
})

// request 拦截器
// 统一加 token；上传文件（FormData）时让浏览器自己设置 multipart 的 Content-Type
request.interceptors.request.use(config => {
    if (!(config.data instanceof FormData)) {
        config.headers['Content-Type'] = 'application/json;charset=utf-8';
    }
    let employee = localStorage.getItem("employee") ? JSON.parse(localStorage.getItem("employee")) : null
    if (employee) {
        config.headers['token'] = employee.token;  // 设置请求头
    }
    return config
}, error => {
    return Promise.reject(error)
});

// response 拦截器
// 可以在接口响应后统一处理结果
request.interceptors.response.use(
    response => {
        let res = response.data;
        // 如果是返回的文件
        if (response.config.responseType === 'blob') {
            return res
        }
        // 兼容服务端返回的字符串数据
        if (typeof res === 'string') {
            res = res ? JSON.parse(res) : res
        }
        //当权限验证不通过时的提示
        if (res.code === 401) {
            ElementUI.Message({
                showClose: true,
                message: res.msg,
                type: 'error',
            })
            // token 过期或无效：清掉登录信息回到登录页（整页刷新，顺便清掉动态路由）
            if (/TOKEN|登录/.test(res.msg || '')) {
                ['employee', 'menus', 'token', 'location'].forEach(key => localStorage.removeItem(key))
                window.location.href = process.env.BASE_URL + 'login'
            }
        }
        return res;
    },
    error => {
        ElementUI.Message({
            showClose: true,
            message: '连接后端失败，请确认后端已启动（' + error.message + '）',
            type: 'error',
        })
        return Promise.reject(error)
    }
)

// 下载后端生成的文件（导出 Excel 等），需要带 token，所以不能直接用 <a href>
export const download = (url, fileName) => {
    return request.get(url, {responseType: 'blob'}).then(blob => {
        const link = document.createElement('a')
        link.href = URL.createObjectURL(blob)
        link.download = fileName
        document.body.appendChild(link)
        link.click()
        document.body.removeChild(link)
        URL.revokeObjectURL(link.href)
    })
}

// 人脸照片地址（/download 不需要 token，可以直接给 <img> 用）
export const photoUrl = name => request.defaults.baseURL + '/download?name=' + encodeURIComponent(name)

export default request
