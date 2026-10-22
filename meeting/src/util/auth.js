import router, {setRoutes} from '@/router'

// 当前登录的员工（登录接口返回的数据，带 token）
export const currentEmployee = () => {
    const text = localStorage.getItem('employee')
    return text ? JSON.parse(text) : {}
}

// 保存登录结果，按菜单生成路由后进入首页
export const saveLogin = data => {
    const {menus, ...employee} = data
    localStorage.setItem('employee', JSON.stringify(employee))
    localStorage.setItem('menus', JSON.stringify(menus || []))
    setRoutes()
    router.push('/home')
}
