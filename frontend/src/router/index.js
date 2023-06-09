import {createRouter, createWebHistory} from 'vue-router'
import {useUserStore} from "@/stores";
import {ElMessage} from "element-plus";

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'welcome',
            component: () => import('@/views/WelcomeView.vue'),
            meta: {
                welcome: true,
                title: '线上图书馆-欢迎'
            },
            children: [
                {
                    path: '',
                    name: 'welcome-login',
                    component: () => import('@/components/welcome/LoginPage.vue')
                },
                {
                    path: 'register',
                    name: 'welcome-register',
                    component: () => import('@/components/welcome/RegisterPage.vue')
                },
                {
                    path: 'forget',
                    name: 'welcome-forget',
                    component: () => import('@/components/welcome/ForgetPage.vue')
                }
            ]
        },
        {
            path: '/admin',
            name: 'admin',
            redirect: 'admin/home',
            meta: {
                requireAdmin: true,
                title: '线上图书馆-管理端'
            },
            component: () => import('@/views/layout/AdminLayout.vue'),
            children: [
                {
                    path: 'home',
                    name: 'admin-home',
                    component: () => import('@/views/admin/HomeView.vue')
                },
                {
                    path: 'book',
                    name: 'book',
                    component: () => import('@/views/admin/BookView.vue')
                },
                {
                    path: 'borrow',
                    name: 'borrow',
                    component: () => import('@/views/admin/BorrowView.vue')
                },
                {
                    path: 'userAdmin',
                    name: 'user-admin',
                    component: () => import('@/views/admin/UserView.vue')
                },
                {
                    path: 'changePassword',
                    name: 'change-password',
                    component: () => import('@/views/admin/ChangePasswordView.vue')
                }
            ]
        },
        {
            path: '/user',
            name: 'user',
            redirect: 'user/home',
            meta: {
                requireUser: true,
                title: '尽情借阅-就在线上图书馆'
            },
            component: () => import('@/views/layout/UserLayout.vue'),
            children: [
                {
                    path: 'home',
                    name: 'user-home',
                    component: () => import('@/views/user/HomeView.vue')
                },
                {
                    path: 'bookSearchList',
                    name: 'book-searchList',
                    component: () => import('@/views/user/BookSearchList.vue'),
                },
                {
                    path: 'hotBorrow',
                    name: 'hot-borrow',
                    component: () => import('@/views/user/HotBorrowView.vue')
                },
                {
                    path: 'me',
                    name: 'user-me',
                    component: () => import('@/views/user/PersonalView.vue'),
                    children: [
                        {
                            path: 'myBorrow',
                            name: 'my-borrow',
                            component: () => import('@/components/user/PersonalBorrowPage.vue')
                        },
                        {
                            path: 'myInfo',
                            name: 'my-info',
                            component: () => import('@/components/user/PersonalInfoPage.vue'),
                        },
                        {
                            path: 'changePwd',
                            name: 'change-pwd',
                            component: () => import('@/components/user/UserChangePwdPage.vue'),
                        },
                    ]
                },
                {
                    path: 'bookDetails/:book_id',
                    name: 'book-details',
                    component: () => import('@/views/user/BookDetails.vue')
                }

            ]
        }
    ]
})

router.beforeEach((to, from, next) => {
    const userStore = useUserStore()

    userStore.updateUserInfo()

    const requireAdmin = to.matched.some(record => record.meta.requireAdmin)
    const requireUser = to.matched.some(record => record.meta.requireUser)
    const isWelcome = to.matched.some(record => record.meta.welcome)

    if (to.meta.title){
        document.title = to.meta.title
    }

    if (userStore.isLogin) {
        const {role} = userStore.userInfo
        if (isWelcome) {
            ElMessage.info('已经登录，无需进入登录页面')
            if (role === 'admin'){
                next('/admin')
            }else {
                next('/user')
            }
        } else if (role !== 'admin' && requireAdmin) {
            ElMessage.warning('无权访问管理页面')
            next('/user')
        } else if (role === 'admin' && requireUser) {
            ElMessage.warning('管理员无法访问用户页面')
            next('/admin')
        } else {
            next()
        }
    } else {
        if (isWelcome) {
            next()
        } else {
            ElMessage.info('请先登录')
            next({name: 'welcome-login'})
        }
    }
})

export default router
