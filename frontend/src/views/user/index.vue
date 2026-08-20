<template>
  <div class="page-container">
    <!-- 搜索区 -->
    <div class="search-bar">
      <el-input v-model="query.username" placeholder="用户名" clearable class="search-input" @keyup.enter="handleSearch" />
      <el-input v-model="query.phone" placeholder="手机号" clearable class="search-input" @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        <span>搜索</span>
      </el-button>
      <el-button @click="handleReset">重置</el-button>
      <el-button type="primary" class="add-btn" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        <span>新增用户</span>
      </el-button>
    </div>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="username" label="用户名" min-width="140">
          <template #default="{ row }">
            <div class="user-cell">
              <div class="user-avatar">{{ row.username?.charAt(0)?.toUpperCase() }}</div>
              <span>{{ row.username }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="phone" label="手机号" min-width="140" />
        <el-table-column prop="isAdmin" label="管理员" width="100">
          <template #default="{ row }">
            <span class="badge" :class="row.isAdmin ? 'badge-primary' : 'badge-default'">
              {{ row.isAdmin ? '是' : '否' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="userType" label="类型" width="110">
          <template #default="{ row }">
            <span class="badge" :class="row.userType === 2 ? 'badge-warning' : 'badge-primary'">
              {{ row.userType === 2 ? '普通用户' : '后台用户' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column prop="createTime" label="创建时间" width="180" />
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <button class="action-btn" @click="handleEdit(row)">编辑</button>
              <button class="action-btn action-btn--danger" @click="handleDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>
      </el-table>
      <div class="pagination-wrap">
        <el-pagination
          v-model:current-page="pageNum"
          v-model:page-size="pageSize"
          :total="total"
          :page-sizes="[10, 20, 50, 100]"
          layout="total, sizes, prev, pager, next, jumper"
          background
          @current-change="loadData"
          @size-change="handleSizeChange"
        />
      </div>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑用户' : '新增用户'" width="480px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="flat-form">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="form.username" :disabled="isEdit" placeholder="请输入用户名" />
        </el-form-item>
        <el-form-item label="密码" prop="password" v-if="!isEdit">
          <el-input v-model="form.password" type="password" show-password placeholder="请输入密码" />
        </el-form-item>
        <el-form-item label="手机号" prop="phone">
          <el-input v-model="form.phone" placeholder="请输入手机号" />
        </el-form-item>
        <el-form-item label="管理员" v-if="form.userType === 1">
          <el-switch v-model="form.isAdmin" />
        </el-form-item>
        <el-form-item label="类型">
          <el-select v-model="form.userType" placeholder="请选择用户类型" style="width: 100%">
            <el-option label="后台用户" :value="1" />
            <el-option label="普通用户" :value="2" />
          </el-select>
        </el-form-item>
      </el-form>
      <template #footer>
        <el-button @click="dialogVisible = false">取消</el-button>
        <el-button type="primary" :loading="submitLoading" @click="handleSubmit">确定</el-button>
      </template>
    </el-dialog>
  </div>
</template>

<script setup>
import { ref, watch, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getUserPage, createUser, updateUser, deleteUser } from '@/api/user'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const query = ref({ username: '', phone: '' })

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function handleReset() {
  query.value = { username: '', phone: '' }
  pageNum.value = 1
  loadData()
}

function handleSizeChange() {
  pageNum.value = 1
  loadData()
}

const form = ref({ username: '', password: '', phone: '', isAdmin: false, userType: 1 })

// 切换为普通用户时自动取消管理员
watch(
  () => form.value.userType,
  (val) => {
    if (val !== 1) form.value.isAdmin = false
  }
)
const rules = {
  username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
  password: [{ required: true, message: '请输入密码', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const res = await getUserPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      username: query.value.username,
      phone: query.value.phone,
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  form.value = { username: '', password: '', phone: '', isAdmin: false, userType: 1 }
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  form.value = { id: row.id, username: row.username, phone: row.phone, isAdmin: row.isAdmin, userType: row.userType || 1 }
  dialogVisible.value = true
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该用户？此操作不可撤销。', '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  })
  await deleteUser(row.id)
  ElMessage.success('删除成功')
  if (tableData.value.length === 1 && pageNum.value > 1) pageNum.value -= 1
  loadData()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateUser(form.value)
      ElMessage.success('更新成功')
    } else {
      await createUser(form.value)
      ElMessage.success('创建成功')
    }
    dialogVisible.value = false
    loadData()
  } finally {
    submitLoading.value = false
  }
}

onMounted(loadData)
</script>

<style lang="scss" scoped>
.search-bar {
  display: flex;
  flex-wrap: wrap;
  gap: 12px;
  align-items: center;
  margin-bottom: 16px;

  .search-input {
    width: 200px;
  }

  .add-btn {
    margin-left: auto;
  }
}

.table-card {
  background: #fff;
  border-radius: 14px;
  box-shadow: 0 1px 3px rgba(0, 0, 0, 0.04);
  overflow: hidden;
  padding: 12px;
}

.pagination-wrap {
  display: flex;
  justify-content: flex-end;
  padding: 16px 4px 4px;
}

.user-cell {
  display: flex;
  align-items: center;
  gap: 10px;
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 8px;
  background: #e8f4fd;
  color: #0071e3;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 13px;
  font-weight: 600;
  flex-shrink: 0;
}

.badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;

  &.badge-primary {
    background: #e8f4fd;
    color: #0071e3;
  }

  &.badge-default {
    background: #f5f5f7;
    color: #86868b;
  }

  &.badge-warning {
    background: #fff4e6;
    color: #ff9500;
  }
}

.action-btns {
  display: flex;
  gap: 8px;
}

.action-btn {
  border: none;
  background: none;
  color: #0071e3;
  font-size: 13px;
  font-weight: 500;
  cursor: pointer;
  padding: 4px 0;
  transition: opacity 0.2s;

  &:hover {
    opacity: 0.7;
  }

  &--danger {
    color: #ff3b30;
  }
}

.flat-form {
  :deep(.el-form-item__label) {
    font-weight: 500;
    color: #1d1d1f;
  }
}
</style>
