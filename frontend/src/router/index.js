import { createRouter, createWebHistory } from 'vue-router'

const routes = [
    {
        path: '/',
        name: 'Home',
        component: () => import('../views/Home.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('../views/Login.vue'),
        meta: { guest: true }
    },
    {
        path: '/register',
        name: 'Register',
        component: () => import('../views/Register.vue'),
        meta: { guest: true }
    },
    {
        path: '/tasks',
        name: 'Tasks',
        component: () => import('../views/Tasks.vue'),
        meta: { requiresAuth: true }
    },
    {
        path: '/categories',
        name: 'Categories',
        component: () => import('../views/Categories.vue'),
        meta: { requiresAuth: true }
    },
    // {
    //     path: '/profile',
    //     name: 'Profile',
    //     component: () => import('../views/Profile.vue'),
    //     meta: { requiresAuth: true }
    // }
]

const router = createRouter({
    history: createWebHistory(),
    routes
})

// 路由守卫
router.beforeEach((to, from, next) => {
    const isAuthenticated = localStorage.getItem('token')

    if (to.matched.some(record => record.meta.requiresAuth)) {
        if (!isAuthenticated) {
            next({ name: 'Login' })
        } else {
            next()
        }
    } else if (to.matched.some(record => record.meta.guest)) {
        if (isAuthenticated) {
            next({ name: 'Home' })
        } else {
            next()
        }
    } else {
        next()
    }
})

export default router