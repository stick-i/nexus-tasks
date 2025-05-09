import { defineStore } from 'pinia'
import axios from 'axios'

// 定义API基础URL
const API_URL = 'http://localhost:8080/api'

// 用户认证存储
export const useAuthStore = defineStore('auth', {
    state: () => ({
        user: JSON.parse(localStorage.getItem('user')) || null,
        token: localStorage.getItem('token') || null,
        loading: false,
        error: null
    }),
    getters: {
        isAuthenticated: (state) => !!state.token,
        getUser: (state) => state.user
    },
    actions: {
        async login(credentials) {
            this.loading = true
            this.error = null
            try {
                const response = await axios.post(`${API_URL}/auth/login`, credentials)
                this.user = response.data.user
                this.token = response.data.token
                localStorage.setItem('user', JSON.stringify(response.data.user))
                localStorage.setItem('token', response.data.token)
                // 设置axios默认请求头
                axios.defaults.headers.common['Authorization'] = `Bearer ${this.token}`
                return response.data
            } catch (error) {
                this.error = error.response?.data?.message || '登录失败'
                throw error
            } finally {
                this.loading = false
            }
        },
        async register(userData) {
            this.loading = true
            this.error = null
            try {
                const response = await axios.post(`${API_URL}/auth/register`, userData)
                return response.data
            } catch (error) {
                this.error = error.response?.data?.message || '注册失败'
                throw error
            } finally {
                this.loading = false
            }
        },
        logout() {
            this.user = null
            this.token = null
            localStorage.removeItem('user')
            localStorage.removeItem('token')
            delete axios.defaults.headers.common['Authorization']
        }
    }
})

// 任务管理存储
export const useTaskStore = defineStore('task', {
    state: () => ({
        tasks: [],
        currentTask: null,
        loading: false,
        error: null
    }),
    getters: {
        getTasks: (state) => state.tasks,
        getTaskById: (state) => (id) => state.tasks.find(task => task.id === id),
        getTasksByStatus: (state) => (status) => state.tasks.filter(task => task.status === status),
        getTasksByPriority: (state) => (priority) => state.tasks.filter(task => task.priority === priority)
    },
    actions: {
        async fetchTasks() {
            this.loading = true
            this.error = null
            try {
                const response = await axios.get(`${API_URL}/tasks`)
                this.tasks = response.data
                return response.data
            } catch (error) {
                this.error = error.response?.data?.message || '获取任务失败'
                throw error
            } finally {
                this.loading = false
            }
        },
        async fetchTaskById(id) {
            this.loading = true
            this.error = null
            try {
                const response = await axios.get(`${API_URL}/tasks/${id}`)
                this.currentTask = response.data
                return response.data
            } catch (error) {
                this.error = error.response?.data?.message || '获取任务详情失败'
                throw error
            } finally {
                this.loading = false
            }
        },
        async createTask(taskData) {
            this.loading = true
            this.error = null
            try {
                const response = await axios.post(`${API_URL}/tasks`, taskData)
                this.tasks.push(response.data)
                return response.data
            } catch (error) {
                this.error = error.response?.data?.message || '创建任务失败'
                throw error
            } finally {
                this.loading = false
            }
        },
        async updateTask(id, taskData) {
            this.loading = true
            this.error = null
            try {
                const response = await axios.put(`${API_URL}/tasks/${id}`, taskData)
                const index = this.tasks.findIndex(task => task.id === id)
                if (index !== -1) {
                    this.tasks[index] = response.data
                }
                return response.data
            } catch (error) {
                this.error = error.response?.data?.message || '更新任务失败'
                throw error
            } finally {
                this.loading = false
            }
        },
        async deleteTask(id) {
            this.loading = true
            this.error = null
            try {
                await axios.delete(`${API_URL}/tasks/${id}`)
                this.tasks = this.tasks.filter(task => task.id !== id)
                return true
            } catch (error) {
                this.error = error.response?.data?.message || '删除任务失败'
                throw error
            } finally {
                this.loading = false
            }
        }
    }
})

// 分类管理存储
export const useCategoryStore = defineStore('category', {
    state: () => ({
        categories: [],
        loading: false,
        error: null
    }),
    getters: {
        getCategories: (state) => state.categories,
        getCategoryById: (state) => (id) => state.categories.find(category => category.id === id)
    },
    actions: {
        async fetchCategories() {
            this.loading = true
            this.error = null
            try {
                const response = await axios.get(`${API_URL}/categories`)
                this.categories = response.data
                return response.data
            } catch (error) {
                this.error = error.response?.data?.message || '获取分类失败'
                throw error
            } finally {
                this.loading = false
            }
        },
        async createCategory(categoryData) {
            this.loading = true
            this.error = null
            try {
                const response = await axios.post(`${API_URL}/categories`, categoryData)
                this.categories.push(response.data)
                return response.data
            } catch (error) {
                this.error = error.response?.data?.message || '创建分类失败'
                throw error
            } finally {
                this.loading = false
            }
        },
        async updateCategory(id, categoryData) {
            this.loading = true
            this.error = null
            try {
                const response = await axios.put(`${API_URL}/categories/${id}`, categoryData)
                const index = this.categories.findIndex(category => category.id === id)
                if (index !== -1) {
                    this.categories[index] = response.data
                }
                return response.data
            } catch (error) {
                this.error = error.response?.data?.message || '更新分类失败'
                throw error
            } finally {
                this.loading = false
            }
        },
        async deleteCategory(id) {
            this.loading = true
            this.error = null
            try {
                await axios.delete(`${API_URL}/categories/${id}`)
                this.categories = this.categories.filter(category => category.id !== id)
                return true
            } catch (error) {
                this.error = error.response?.data?.message || '删除分类失败'
                throw error
            } finally {
                this.loading = false
            }
        }
    }
})