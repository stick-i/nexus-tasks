<template>
    <div class="categories-container">
        <el-card class="categories-card">
            <template #header>
                <div class="card-header">
                    <h2>分类管理</h2>
                    <el-button type="primary" @click="dialogVisible = true">新建分类</el-button>
                </div>
            </template>

            <!-- 分类列表 -->
            <el-table v-loading="loading" :data="categories" style="width: 100%"
                :empty-text="loading ? '加载中...' : '暂无分类数据'">
                <el-table-column prop="name" label="名称" min-width="120" />
                <el-table-column prop="description" label="描述" min-width="200" />

                <el-table-column prop="createdAt" label="创建时间" width="180">
                    <template #default="{ row }">
                        {{ formatDate(row.createdAt) }}
                    </template>
                </el-table-column>

                <el-table-column label="任务数量" width="100">
                    <template #default="{ row }">
                        {{ getCategoryTaskCount(row.id) }}
                    </template>
                </el-table-column>

                <el-table-column label="操作" width="150" fixed="right">
                    <template #default="{ row }">
                        <el-button-group>
                            <el-button size="small" type="primary" @click="editCategory(row)">编辑</el-button>
                            <el-button size="small" type="danger" @click="confirmDelete(row)">删除</el-button>
                        </el-button-group>
                    </template>
                </el-table-column>
            </el-table>
        </el-card>

        <!-- 新建/编辑分类对话框 -->
        <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑分类' : '新建分类'" width="500px" destroy-on-close>
            <el-form :model="categoryForm" :rules="rules" ref="categoryFormRef" label-width="80px">
                <el-form-item label="名称" prop="name">
                    <el-input v-model="categoryForm.name" placeholder="请输入分类名称"></el-input>
                </el-form-item>

                <el-form-item label="描述" prop="description">
                    <el-input v-model="categoryForm.description" type="textarea" rows="3"
                        placeholder="请输入分类描述"></el-input>
                </el-form-item>
            </el-form>

            <template #footer>
                <div class="dialog-footer">
                    <el-button @click="dialogVisible = false">取消</el-button>
                    <el-button type="primary" :loading="submitLoading" @click="submitCategory">确定</el-button>
                </div>
            </template>
        </el-dialog>
    </div>
</template>

<script setup>
import { ref, reactive, computed, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { useCategoryStore, useTaskStore } from '../stores'
import { format } from 'date-fns'

const categoryStore = useCategoryStore()
const taskStore = useTaskStore()

// 状态变量
const loading = computed(() => categoryStore.loading)
const submitLoading = ref(false)
const dialogVisible = ref(false)
const isEdit = ref(false)
const categoryFormRef = ref(null)

// 表单数据
const categoryForm = reactive({
    id: null,
    name: '',
    description: ''
})

// 表单验证规则
const rules = {
    name: [
        { required: true, message: '请输入分类名称', trigger: 'blur' },
        { min: 2, max: 50, message: '名称长度应在2到50个字符之间', trigger: 'blur' }
    ]
}

// 计算属性
const categories = computed(() => categoryStore.categories)
const tasks = computed(() => taskStore.tasks)

// 生命周期钩子
onMounted(async () => {
    try {
        await Promise.all([
            categoryStore.fetchCategories(),
            taskStore.fetchTasks()
        ])
    } catch (error) {
        ElMessage.error('加载数据失败')
    }
})

// 方法
const formatDate = (dateString) => {
    if (!dateString) return '未知'
    try {
        return format(new Date(dateString), 'yyyy-MM-dd HH:mm')
    } catch (error) {
        return dateString
    }
}

const getCategoryTaskCount = (categoryId) => {
    return tasks.value.filter(task => task.category?.id === categoryId).length
}

const resetCategoryForm = () => {
    categoryForm.id = null
    categoryForm.name = ''
    categoryForm.description = ''
}

const editCategory = (category) => {
    isEdit.value = true
    categoryForm.id = category.id
    categoryForm.name = category.name
    categoryForm.description = category.description
    dialogVisible.value = true
}

const confirmDelete = (category) => {
    // 检查分类下是否有任务
    const taskCount = getCategoryTaskCount(category.id)

    if (taskCount > 0) {
        ElMessageBox.confirm(
            `此分类下有 ${taskCount} 个任务，删除分类将会移除这些任务的分类关联，是否继续？`,
            '警告',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        ).then(() => {
            deleteCategory(category)
        }).catch(() => {
            // 用户取消删除操作
        })
    } else {
        ElMessageBox.confirm(
            '确定要删除这个分类吗？此操作不可撤销。',
            '警告',
            {
                confirmButtonText: '确定',
                cancelButtonText: '取消',
                type: 'warning',
            }
        ).then(() => {
            deleteCategory(category)
        }).catch(() => {
            // 用户取消删除操作
        })
    }
}

const deleteCategory = async (category) => {
    try {
        await categoryStore.deleteCategory(category.id)
        ElMessage.success('分类已删除')
    } catch (error) {
        ElMessage.error('删除分类失败')
    }
}

const submitCategory = async () => {
    if (!categoryFormRef.value) return

    await categoryFormRef.value.validate(async (valid) => {
        if (valid) {
            submitLoading.value = true
            try {
                // 准备提交的数据
                const categoryData = {
                    name: categoryForm.name,
                    description: categoryForm.description
                }

                if (isEdit.value) {
                    await categoryStore.updateCategory(categoryForm.id, categoryData)
                    ElMessage.success('分类更新成功')
                } else {
                    await categoryStore.createCategory(categoryData)
                    ElMessage.success('分类创建成功')
                }

                dialogVisible.value = false
                resetCategoryForm()
            } catch (error) {
                ElMessage.error(isEdit.value ? '更新分类失败' : '创建分类失败')
            } finally {
                submitLoading.value = false
            }
        }
    })
}
</script>

<style scoped>
.categories-container {
    padding: 20px;
}

.categories-card {
    width: 100%;
}

.card-header {
    display: flex;
    justify-content: space-between;
    align-items: center;
}

.el-button-group {
    display: flex;
    gap: 5px;
}
</style>