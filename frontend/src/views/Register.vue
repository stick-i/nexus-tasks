<template>
    <div class="register-container">
        <el-card class="register-card">
            <template #header>
                <div class="card-header">
                    <h2>注册</h2>
                </div>
            </template>
            <el-form :model="registerForm" :rules="rules" ref="registerFormRef" label-position="top">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="registerForm.username" placeholder="请输入用户名"></el-input>
                </el-form-item>
                <el-form-item label="邮箱" prop="email">
                    <el-input v-model="registerForm.email" placeholder="请输入邮箱"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="registerForm.password" type="password" placeholder="请输入密码"></el-input>
                </el-form-item>
                <el-form-item label="确认密码" prop="confirmPassword">
                    <el-input v-model="registerForm.confirmPassword" type="password" placeholder="请再次输入密码"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" :loading="loading" @click="handleRegister"
                        style="width: 100%">注册</el-button>
                </el-form-item>
                <div class="form-footer">
                    <span>已有账号？</span>
                    <router-link to="/login">立即登录</router-link>
                </div>
            </el-form>
        </el-card>
    </div>
</template>

<script setup>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { ElMessage } from 'element-plus'
import { useAuthStore } from '../stores'

const router = useRouter()
const authStore = useAuthStore()
const registerFormRef = ref(null)
const loading = ref(false)

const registerForm = reactive({
    username: '',
    email: '',
    password: '',
    confirmPassword: ''
})

const validatePass = (rule, value, callback) => {
    if (value === '') {
        callback(new Error('请再次输入密码'))
    } else if (value !== registerForm.password) {
        callback(new Error('两次输入密码不一致'))
    } else {
        callback()
    }
}

const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度应在3到20个字符之间', trigger: 'blur' }
    ],
    email: [
        { required: true, message: '请输入邮箱地址', trigger: 'blur' },
        { type: 'email', message: '请输入正确的邮箱地址', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度应在6到20个字符之间', trigger: 'blur' }
    ],
    confirmPassword: [
        { required: true, message: '请再次输入密码', trigger: 'blur' },
        { validator: validatePass, trigger: 'blur' }
    ]
}

const handleRegister = async () => {
    if (!registerFormRef.value) return

    await registerFormRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true
            try {
                // 移除确认密码字段，后端不需要
                const { confirmPassword, ...userData } = registerForm
                await authStore.register(userData)
                ElMessage.success('注册成功，请登录')
                router.push('/login')
            } catch (error) {
                ElMessage.error(error.response?.data?.message || '注册失败，请稍后再试')
            } finally {
                loading.value = false
            }
        }
    })
}
</script>

<style scoped>
.register-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f5f7fa;
}

.register-card {
    width: 400px;
    max-width: 90%;
}

.card-header {
    display: flex;
    justify-content: center;
    align-items: center;
}

.form-footer {
    margin-top: 15px;
    text-align: center;
    font-size: 14px;
}

.form-footer a {
    color: #409eff;
    margin-left: 5px;
}
</style>