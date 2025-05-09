<template>
    <div class="login-container">
        <el-card class="login-card">
            <template #header>
                <div class="card-header">
                    <h2>登录</h2>
                </div>
            </template>
            <el-form :model="loginForm" :rules="rules" ref="loginFormRef" label-position="top">
                <el-form-item label="用户名" prop="username">
                    <el-input v-model="loginForm.username" placeholder="请输入用户名"></el-input>
                </el-form-item>
                <el-form-item label="密码" prop="password">
                    <el-input v-model="loginForm.password" type="password" placeholder="请输入密码"></el-input>
                </el-form-item>
                <el-form-item>
                    <el-button type="primary" :loading="loading" @click="handleLogin" style="width: 100%">登录</el-button>
                </el-form-item>
                <div class="form-footer">
                    <span>还没有账号？</span>
                    <router-link to="/register">立即注册</router-link>
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
const loginFormRef = ref(null)
const loading = ref(false)

const loginForm = reactive({
    username: '',
    password: ''
})

const rules = {
    username: [
        { required: true, message: '请输入用户名', trigger: 'blur' },
        { min: 3, max: 20, message: '用户名长度应在3到20个字符之间', trigger: 'blur' }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 6, max: 20, message: '密码长度应在6到20个字符之间', trigger: 'blur' }
    ]
}

const handleLogin = async () => {
    if (!loginFormRef.value) return

    await loginFormRef.value.validate(async (valid) => {
        if (valid) {
            loading.value = true
            try {
                await authStore.login(loginForm)
                ElMessage.success('登录成功')
                router.push('/')
            } catch (error) {
                ElMessage.error(error.response?.data?.message || '登录失败，请检查用户名和密码')
            } finally {
                loading.value = false
            }
        }
    })
}
</script>

<style scoped>
.login-container {
    display: flex;
    justify-content: center;
    align-items: center;
    height: 100vh;
    background-color: #f5f7fa;
}

.login-card {
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