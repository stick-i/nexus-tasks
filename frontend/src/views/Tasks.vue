<template>
    <div class="tasks-container">
        <el-card class="tasks-card">
            <template #header>
                <div class="card-header">
                    <h2>任务管理</h2>
                    <el-button type="primary" @click="dialogVisible = true">新建任务</el-button>
                </div>
            </template>

            <!-- 任务筛选 -->
            <div class="filter-container">
                <el-select v-model="filterStatus" placeholder="状态筛选" clearable>
                    <el-option label="待办" value="TODO" />
                    <el-option label="进行中" value="IN_PROGRESS" />
                    <el-option label="已完成" value="DONE" />
                </el-select>

                <el-select v-model="filterPriority" placeholder="优先级筛选" clearable>
                    <el-option label="低" value="LOW" />
                    <el-option label="中" value="MEDIUM" />
                    <el-option label="高" value="HIGH" />
                </el-select>

                <el-select v-model="filterCategory" placeholder="分类筛选" clearable>
                    <el-option v-for="category in categories" :key="category.id" :label="category.name"
                        :value="category.id" />
                </el-select>
            </div>

            <!-- 任务列表 -->
            <el-table v-loading="loading" :data="filteredTasks" style="width: 100%"
                :empty-text="loading ? '加载中...' : '暂无任务数据'">
                <el-table-column prop="title" label="标题" min-width="120" />

                <el-table-column prop="status" label="状态" width="100">
                    <template #default="{ row }">
                        <el-tag :type="getStatusType(row.status)">
                            {{ getStatusText(row.status) }}
                        </el-tag>
                    </template>
                </el-table-column>

                <el-table-column prop="priority" label="优先级" width="100">
                    <template #default="{ row }">
                        <el-tag :type="getPriorityType(row.priority)">
                            {{ getPriorityText(row.priority) }}
                        </el-tag>
                    </template>
                </el-table-column>

                <el-table-column prop="category.name" label="分类" width="120" />

                <el-table-column prop="dueDate" label="截止日期" width="180">
                    <template #default="{ row }">
                        {{ formatDate(row.dueDate) }}
                    </template>
                </el-table-column>

                <el-table-column label="操作" width="200" fixed="right">
                    <template #default="{ row }">
                        <el-button-group>
                            <el-button size="small" type="primary" @click="editTask(row)">编辑</el-button>
                            <el-button size="small" type="success" @click="changeStatus(row)">
                                {{ row.status === 'DONE' ? '重新开始' : '完成' }}
                            </el-button>
                            <el-button size="small" type="danger" @click="confirmDelete(row)">删除</el-button>
                        </el-button-group>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 新建/编辑任务对话框 -->
        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑任务' : '新建任务'" width="500px" destroy-on-close>
            <el-form :model="taskForm" :rules="rules" ref="taskFormRef" label-width="80px">
                <el-form-item label="标题" prop="title">
                    <el-input v-model="taskForm.title" placeholder="请输入任务标题"></el-input>
                </el-form-item>

                <el-form-item label="描述" prop="description">
                    <el-input v-model="taskForm.description" type="textarea" rows="3" placeholder="请输入任务描述"></el-input>
                </el-form-item>

                <el-form-item label="状态" prop="status">
                    <el-select v-model="taskForm.status" placeholder="请选择任务状态" style="width: 100%">
                        <el-option label="待办" value="TODO" />
                        <el-option label="进行中" value="IN_PROGRESS" />
                        <el-option label="已完成" value="DONE" />
                    </el-select>
                </el-form-item>

                <el-form-item label="优先级" prop="priority">
                    <el-select v-model="taskForm.priority" placeholder="请选择任务优先级" style="width: 100%">
                        <el-option label="低" value="LOW" />
                        <el-option label="中" value="MEDIUM" />
                        <el-option label="高" value="HIGH" />
                    </el-select>
                </el-form-item>

                <el-form-item label="分类" prop="categoryId">
                    <el-select v-model="taskForm.categoryId" placeholder="请选择任务分类" style="width: 100%">
                        <el-option v-for="category in categories" :key="category.id" :label="category.name"
                            :value="category.id" />
                    </el-select>
                </el-form-item>

                <el-form-item label="截止日期" prop="dueDate">
                    <el-date-picker v-model="taskForm.dueDate" type="datetime" placeholder="请选择截止日期"
                        style="width: 100%"></el-date-picker>
                </el-form-item>
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" :loading="submitLoading" @click="submitTask">确定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useTaskStore, useCategoryStore } from '../stores'
import { format } from 'date-fns'

const taskStore = useTaskStore()
const categoryStore = useCategoryStore()

// 状态变量
const loading = computed(() => taskStore.loading)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const taskFormRef = ref(null)

// 筛选条件
const filterStatus = ref('')
const filterPriority = ref('')
const filterCategory = ref('')

// 表单数据
const taskForm = reactive({
    id: null,
    title: '',
    description: '',
    status: 'TODO',
    priority: 'MEDIUM',
    categoryId: '',
    dueDate: ''
})

// 表单验证规则
const rules = {
    title: [
        { required: true, message: '请输入任务标题', trigger: 'blur' },
        { min: 2, max: 50, message: '标题长度应在2到50个字符之间', trigger: 'blur' }
    ],
    status: [{ required: true, message: '请选择任务状态', trigger: 'change' }],
    priority: [{ required: true, message: '请选择任务优先级', trigger: 'change' }],
    dueDate: [{ required: true, message: '请选择截止日期', trigger: 'change' }]
}

// 计算属性
const tasks = computed(() => taskStore.tasks)
const categories = computed(() => categoryStore.categories)

// 筛选后的任务列表
const filteredTasks = computed(() => {
    let result = [...tasks.value]

    if (filterStatus.value) {
        result = result.filter(task => task.status === filterStatus.value)
    }

    if (filterPriority.value) {
        result = result.filter(task => task.priority === filterPriority.value)
    }

    if (filterCategory.value) {
        result = result.filter(task => task.category?.id === filterCategory.value)
    }

    return result
})

// 生命周期钩子
onMounted(async () => {
    try {
        await Promise.all([
            taskStore.fetchTasks(),
            categoryStore.fetchCategories()
        ])
    } catch (error) {
        ElMessage.error('加载数据失败')
    }
})

// 方法
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

const getPriorityType = (priority) => {
    const types = {
        'LOW': 'info',
        'MEDIUM': 'warning',
        'HIGH': 'danger'
    }
    return types[priority] || 'info'
}

const getPriorityText = (priority) => {
    const texts = {
        'LOW': '低',
        'MEDIUM': '中',
        'HIGH': '高'
    }
    return texts[priority] || '未知'
}

const formatDate = (dateString) => {
    if (!dateString) return '无截止日期'
    try {
        return format(new Date(dateString), 'yyyy-MM-dd HH:mm')
    } catch (error) {
        return dateString
    }
}

const resetTaskForm = () => {
    taskForm.id = null
    taskForm.title = ''
    taskForm.description = ''
    taskForm.status = 'TODO'
    taskForm.priority = 'MEDIUM'
    taskForm.categoryId = ''
    taskForm.dueDate = ''
}

const editTask = (task) => {
    isEdit.value = true
    taskForm.id = task.id
    taskForm.title = task.title
    taskForm.description = task.description
    taskForm.status = task.status
    taskForm.priority = task.priority
    taskForm.categoryId = task.category?.id || ''
    taskForm.dueDate = task.dueDate
    dialogVisible.value = true
}

const changeStatus = async (task) => {
    try {
        const newStatus = task.status === 'DONE' ? 'TODO' : 'DONE'
        await taskStore.updateTask(task.id, { ...task, status: newStatus })
        ElMessage.success(`任务已${newStatus === 'DONE' ? '完成' : '重新开始'}`)
    } catch (error) {
        ElMessage.error('更新任务状态失败')
    }
}

const confirmDelete = (task) => {
    ElMessageBox.confirm(
        '确定要删除这个任务吗？此操作不可撤销。',
        '警告',
        {
            confirmButtonText: '确定',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        try {
            await taskStore.deleteTask(task.id)
            ElMessage.success('任务已删除')
        } catch (error) {
            ElMessage.error('删除任务失败')
        }
    }).catch(() => {
        // 用户取消删除操作
    })
}

const submitTask = async () => {
    if (!taskFormRef.value) return

    await taskFormRef.value.validate(async (valid) => {
        if (valid) {
            submitLoading.value = true
            try {
                // 准备提交的数据
                const taskData = {
                    title: taskForm.title,
                    description: taskForm.description,
                    status: taskForm.status,
                    priority: taskForm.priority,
                    categoryId: taskForm.categoryId,
                    dueDate: taskForm.dueDate
                }

                if (isEdit.value) {
                    await taskStore.updateTask(taskForm.id, taskData)
                    ElMessage.success('任务更新成功')
                } else {
                    await taskStore.createTask(taskData)
                    ElMessage.success('任务创建成功')
                }

                dialogVisible.value = false
                resetTaskForm()
            } catch (error) {
                ElMessage.error(isEdit.value ? '更新任务失败' : '创建任务失败')
            } finally {
                submitLoading.value = false
            }
        }
    })
}
</script>

<style scoped>
.tasks-container {
    padding: 20px;
}

.tasks-card {
    width: 100%;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.filter-container {
    margin-bottom: 20px;
    display: flex;
    gap: 10px;
}

.el-button-group {
    display: flex;
    gap: 5px;
}
</style>