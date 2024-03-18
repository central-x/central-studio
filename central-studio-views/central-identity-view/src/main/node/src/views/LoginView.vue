<script lang="ts" setup>
import { reactive } from 'vue'
import axios from 'axios'
import { sha256 } from 'js-sha256'



const form = reactive({
  account: 'syssa',
  password: 'x.123456',
  secret: 'lLS4p6skBbBVZX30zR5' // Web 端固定密钥
})

const onSubmit = () => {
  // 对密码进行 sha256 摘要后再提交
  // 防止原始密码被截取
  const hasher = sha256.create();
  hasher.update(form.password);

  axios.post('/identity/api/login', {
    account: form.account,
    password: hasher.hex(),
    secret: form.secret
  }).then((response) => {
    // 登录成功后，刷新页面
    // 后台接口在接收到请求后，会根据是否存在 redirect_uri 参数自动判断重定向到指定的地址
    window.location.reload()
  }).catch((error) => {
    // 登录失败
    alert(error.response.data.message)
  })
}
</script>

<template>
  <el-form :model="form" label-width="auto" style="max-width: 600px">
    <el-form-item label="Username">
      <el-input v-model="form.account" />
    </el-form-item>
    <el-form-item label="Password">
      <el-input type="password" v-model="form.password" />
    </el-form-item>
    <el-form-item>
      <el-button type="primary" @click.prevent="onSubmit">Login</el-button>
    </el-form-item>
  </el-form>
</template>
