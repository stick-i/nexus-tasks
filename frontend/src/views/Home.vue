<template>
    <div class="home-container">
        <el-row :gutter="20">
            <el-col :xs="24" :sm="24" :md="24" :lg="24" :xl="24">
                <el-card class="welcome-card">
                    <h2>欢迎回来，{{ username }}</h2>
                    <p>这是您的任务管理中心，您可以在这里查看和管理您的所有任务。</p>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" class="dashboard-row">
            <el-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
                <el-card class="stat-card">
                    <template #header>
                        <div class="card-header">
                            <h3>待办任务</h3>
                        </div>
                    </template>
                    <div class="stat-value">
                        <el-statistic :value="todoCount">
                            <template #suffix>
                                <el-icon>
                                    <Calendar />
                                </el-icon>
                            </template>
                        </el-statistic>
                    </div>
                </el-card>
            </el-col>

            <el-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
                <el-card class="stat-card">
                    <template #header>
                        <div class="card-header">
                            <h3>进行中任务</h3>
                        </div>
                    </template>
                    <div class="stat-value">
                        <el-statistic :value="inProgressCount">
                            <template #suffix>
                                <el-icon>
                                    <Loading />
                                </el-icon>
                            </template>
                        </el-statistic>
                    </div>
                </el-card>
            </el-col>

            <el-col :xs="24" :sm="8" :md="8" :lg="8" :xl="8">
                <el-card class="stat-card">
                    <template #header>
                        <div class="card-header">
                            <h3>已完成任务</h3>
                        </div>
                    </template>
                    <div class="stat-value">
                        <el-statistic :value="doneCount">
                            <template #suffix>
                                <el-icon>
                                    <Check />
                                </el-icon>
                            </template>
                        </el-statistic>
                    </div>
                </el-card>
            </el-col>
        </el-row>

        <el-row :gutter="20" class="dashboard-row">
            <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                <el-card class="task-card">
                    <template #header>
                        <div class="card-header">
                            <h3>即将到期的任务</h3>
                            <router-link to="/tasks">
                                <el-button type="primary" size="small">查看全部</el-button>
                            </router-link>
                        </div>
                    </template>
                    <div v-if="upcomingTasks.length > 0">
                        <el-timeline>
                            <el-timeline-item v-for="task in upcomingTasks" :key="task.id"
                                :type="getPriorityType(task.priority)" :timestamp="formatDate(task.dueDate)">
                                {{ task.title }}
                                <el-tag size="small" class="ml-2" :type="getStatusType(task.status)">
                                    {{ getStatusText(task.status) }}
                                </el-tag>
                            </el-timeline-item>
                        </el-timeline>
                    </div>
                    <el-empty v-else description="暂无即将到期的任务" />
                </el-card>
            </el-col>

            <el-col :xs="24" :sm="24" :md="12" :lg="12" :xl="12">
                <el-card class="category-card">
                    <template #header>
                        <div class="card-header">
                            <h3>任务分类</h3>
                            <router-link to="/categories">
                                <el-button type="primary" size="small">管理分类</el-button>
                            </router-link>
                        </div>
                    </template>
                    <div v-if="categories.length > 0">
                        <el-tag v-for="category in categories" :key="category.id" class="category-tag" effect="plain">
                            {{ category.name }} ({{ getCategoryTaskCount(category.id) }})
                        </el-tag>
                    </div>
                    <el-empty v-else description="暂无分类" />
                </el-card>
            </el-col>
        </el-row>
    </div>
</template>

<script setup>
import { ref, computed, onMounted } from 'vue'
import { useTaskStore, useCategoryStore, useAuthStore } from '../stores'
import { Calendar, Loading, Check } from '@element-plus/icons-vue'
import { format, isAfter, addDays } from 'date-fns'

const taskStore = useTaskStore()
const categoryStore = useCategoryStore()
const authStore = useAuthStore()

// 获取用户名
const username = computed(() => {
    return authStore.user?.username || '用户'
})

// 任务统计
const tasks = computed(() => taskStore.tasks)
const categories = computed(() => categoryStore.categories)

const todoCount = computed(() => {
    return tasks.value.filter(task => task.status === 'TODO').length
})

const inProgressCount = computed(() => {
    return tasks.value.filter(task => task.status === 'IN_PROGRESS').length
})

const doneCount = computed(() => {
    return tasks.value.filter(task => task.status === 'DONE').length
})

// 即将到期的任务（7天内）
const upcomingTasks = computed(() => {
    const now = new Date()
    const nextWeek = addDays(now, 7)

    return tasks.value
        .filter(task => {
            if (!task.dueDate) return false
            const dueDate = new Date(task.dueDate)
            return task.status !== 'DONE' && isAfter(dueDate, now) && !isAfter(dueDate, nextWeek)
        })
        .sort((a, b) => new Date(a.dueDate) - new Date(b.dueDate))
        .slice(0, 5) // 只显示前5个
})

// 获取分类下的任务数量
const getCategoryTaskCount = (categoryId) => {
    return tasks.value.filter(task => task.category?.id === categoryId).length
}

// 格式化日期
const formatDate = (dateString) => {
    if (!dateString) return '无截止日期'
    try {
        return format(new Date(dateString), 'yyyy-MM-dd HH:mm')
    } catch (error) {
        return dateString
    }
}

// 获取状态样式
const getStatusType = (status) => {
    const types = {
        'TODO': 'info',
        'IN_PROGRESS': 'warning',
        'DONE': 'success'
    }
    return types[status] || 'info'
}

const getStatusText = (status) => {
    const texts = {
        'TODO': '待办',
        'IN_PROGRESS': '进行中',
        'DONE': '已完成'
    }
    return texts[status] || '未知'
}

// 获取优先级样式
const getPriorityType = (priority) => {
    const types = {
        'LOW': '',
        'MEDIUM': 'warning',
        'HIGH': 'danger'
    }
    return types[priority] || ''
}

// 加载数据
onMounted(async () => {
    try {
        await Promise.all([
            taskStore.fetchTasks(),
            categoryStore.fetchCategories()
        ])
    } catch (error) {
        console.error('加载数据失败', error)
    }
})
</script>

<style scoped>
.home-container {
    padding: 20px;
}

.welcome-card {
    margin-bottom: 20px;
    background-color: #f0f9ff;
}

.dashboard-row {
    margin-bottom: 20px;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.stat-card {
    text-align: center;
    height: 100%;
}

.stat-value {
    font-size: 24px;
    font-weight: bold;
}

.category-tag {
    margin-right: 10px;
    margin-bottom: 10px;
    cursor: pointer;
}

.ml-2 {
    margin-left: 8px;
}
</style>