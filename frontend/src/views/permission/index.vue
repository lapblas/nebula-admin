<template>
  <div class="page-container">
    <!-- 搜索区 -->
    <div class="search-bar">
      <el-input v-model="query.permissionName" placeholder="权限名称" clearable class="search-input" @keyup.enter="handleSearch" />
      <el-input v-model="query.permissionKey" placeholder="权限标识" clearable class="search-input" @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        <span>搜索</span>
      </el-button>
      <el-button @click="handleReset">重置</el-button>
      <el-button type="primary" class="add-btn" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        <span>新增权限</span>
      </el-button>
    </div>

    <div class="table-card">
      <el-table :data="tableData" v-loading="loading" stripe>
        <el-table-column prop="id" label="ID" width="80" />
        <el-table-column prop="permissionName" label="权限名称" min-width="140">
          <template #default="{ row }">
            <span class="perm-name">{{ row.permissionName }}</span>
          </template>
        </el-table-column>
        <el-table-column prop="permissionKey" label="权限标识" min-width="200">
          <template #default="{ row }">
            <code class="key-tag">{{ row.permissionKey }}</code>
          </template>
        </el-table-column>
        <el-table-column prop="description" label="描述" min-width="200" />
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

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑权限' : '新增权限'" width="480px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="flat-form">
        <el-form-item label="权限名称" prop="permissionName">
          <el-input v-model="form.permissionName" placeholder="如：添加用户" />
        </el-form-item>
        <el-form-item label="权限标识" prop="permissionKey">
          <el-input v-model="form.permissionKey" :disabled="isEdit" placeholder="如：system:user:add" />
        </el-form-item>
        <el-form-item label="描述">
          <el-input v-model="form.description" type="textarea" :rows="3" placeholder="请输入权限描述" />
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
import { ref, onMounted } from 'vue'
import { ElMessage, ElMessageBox } from 'element-plus'
import { Search } from '@element-plus/icons-vue'
import { getPermissionPage, createPermission, updatePermission, deletePermission } from '@/api/permission'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const total = ref(0)
const pageNum = ref(1)
const pageSize = ref(10)
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const query = ref({ permissionName: '', permissionKey: '' })

function handleSearch() {
  pageNum.value = 1
  loadData()
}

function handleReset() {
  query.value = { permissionName: '', permissionKey: '' }
  pageNum.value = 1
  loadData()
}

function handleSizeChange() {
  pageNum.value = 1
  loadData()
}

const form = ref({ permissionName: '', permissionKey: '', description: '' })
const rules = {
  permissionName: [{ required: true, message: '请输入权限名称', trigger: 'blur' }],
  permissionKey: [{ required: true, message: '请输入权限标识', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    const res = await getPermissionPage({
      pageNum: pageNum.value,
      pageSize: pageSize.value,
      permissionName: query.value.permissionName,
      permissionKey: query.value.permissionKey,
    })
    tableData.value = res.data?.records || []
    total.value = res.data?.total || 0
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  form.value = { permissionName: '', permissionKey: '', description: '' }
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  form.value = { id: row.id, permissionName: row.permissionName, permissionKey: row.permissionKey, description: row.description }
  dialogVisible.value = true
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该权限？此操作不可撤销。', '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  })
  await deletePermission(row.id)
  ElMessage.success('删除成功')
  if (tableData.value.length === 1 && pageNum.value > 1) pageNum.value -= 1
  loadData()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updatePermission(form.value)
      ElMessage.success('更新成功')
    } else {
      await createPermission(form.value)
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

.perm-name {
  font-weight: 600;
  color: #1d1d1f;
}

.key-tag {
  background: #f5f5f7;
  padding: 3px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-family: 'SF Mono', 'Fira Code', monospace;
  color: #ff9500;
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
