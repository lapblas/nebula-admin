<template>
  <div class="page-container">
    <!-- 搜索区 -->
    <div class="search-bar">
      <el-input v-model="query.menuName" placeholder="菜单名称" clearable class="search-input" @keyup.enter="handleSearch" />
      <el-input v-model="query.path" placeholder="路由路径" clearable class="search-input" @keyup.enter="handleSearch" />
      <el-button type="primary" @click="handleSearch">
        <el-icon><Search /></el-icon>
        <span>搜索</span>
      </el-button>
      <el-button @click="handleReset">重置</el-button>
      <el-button type="primary" class="add-btn" @click="handleAdd">
        <el-icon><Plus /></el-icon>
        <span>新增菜单</span>
      </el-button>
    </div>

    <div class="table-card">
      <el-table
        :data="tableData"
        v-loading="loading"
        row-key="id"
        :tree-props="{ children: 'children' }"
        default-expand-all
      >
        <el-table-column prop="menuName" label="菜单名称" min-width="200">
          <template #default="{ row }">
            <div class="menu-cell">
              <el-icon v-if="row.icon" class="menu-icon"><component :is="row.icon" /></el-icon>
              <span class="menu-name">{{ row.menuName }}</span>
            </div>
          </template>
        </el-table-column>
        <el-table-column prop="path" label="路由路径" min-width="160">
          <template #default="{ row }">
            <code v-if="row.path" class="path-tag">{{ row.path }}</code>
            <span v-else class="text-muted">-</span>
          </template>
        </el-table-column>
        <el-table-column prop="sort" label="排序" width="80" />
        <el-table-column prop="visible" label="可见" width="80">
          <template #default="{ row }">
            <span class="badge" :class="row.visible !== false ? 'badge-success' : 'badge-default'">
              {{ row.visible !== false ? '是' : '否' }}
            </span>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="160" fixed="right">
          <template #default="{ row }">
            <div class="action-btns">
              <button class="action-btn" @click="handleEdit(row)">编辑</button>
              <button class="action-btn action-btn--danger" @click="handleDelete(row)">删除</button>
            </div>
          </template>
        </el-table-column>
      </el-table>
    </div>

    <el-dialog v-model="dialogVisible" :title="isEdit ? '编辑菜单' : '新增菜单'" width="480px" :close-on-click-modal="false">
      <el-form ref="formRef" :model="form" :rules="rules" label-width="90px" class="flat-form">
        <el-form-item label="菜单名称" prop="menuName">
          <el-input v-model="form.menuName" placeholder="请输入菜单名称" />
        </el-form-item>
        <el-form-item label="上级菜单">
          <el-tree-select
            v-model="form.parentId"
            :data="menuTreeData"
            :props="{ label: 'menuName', value: 'id', children: 'children' }"
            placeholder="无（顶级菜单）"
            clearable
            check-strictly
          />
        </el-form-item>
        <el-form-item label="路由路径">
          <el-input v-model="form.path" placeholder="/example" />
        </el-form-item>
        <el-form-item label="图标">
          <el-input v-model="form.icon" placeholder="如：User、Setting" />
        </el-form-item>
        <el-form-item label="排序">
          <el-input-number v-model="form.sort" :min="0" />
        </el-form-item>
        <el-form-item label="是否可见">
          <el-switch v-model="form.visible" />
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
import { getMenuTree, createMenu, updateMenu, deleteMenu } from '@/api/menu'

const loading = ref(false)
const submitLoading = ref(false)
const tableData = ref([])
const menuTreeData = ref([])
const dialogVisible = ref(false)
const isEdit = ref(false)
const formRef = ref(null)

const query = ref({ menuName: '', path: '' })

function handleSearch() {
  loadData()
}

function handleReset() {
  query.value = { menuName: '', path: '' }
  loadData()
}

const form = ref({ menuName: '', parentId: null, path: '', icon: '', sort: 0, visible: true })
const rules = {
  menuName: [{ required: true, message: '请输入菜单名称', trigger: 'blur' }],
}

async function loadData() {
  loading.value = true
  try {
    // 表格数据：按搜索条件服务端过滤，保留祖先节点的树形结构
    const treeRes = await getMenuTree({
      menuName: query.value.menuName,
      path: query.value.path,
    })
    tableData.value = treeRes.data || []
    // 上级菜单下拉：始终使用完整树
    const allRes = await getMenuTree()
    menuTreeData.value = allRes.data || []
  } finally {
    loading.value = false
  }
}

function handleAdd() {
  isEdit.value = false
  form.value = { menuName: '', parentId: null, path: '', icon: '', sort: 0, visible: true }
  dialogVisible.value = true
}

function handleEdit(row) {
  isEdit.value = true
  form.value = { id: row.id, menuName: row.menuName, parentId: row.parentId, path: row.path, icon: row.icon, sort: row.sort, visible: row.visible !== false }
  dialogVisible.value = true
}

async function handleDelete(row) {
  await ElMessageBox.confirm('确定删除该菜单？此操作不可撤销。', '删除确认', {
    confirmButtonText: '删除',
    cancelButtonText: '取消',
    type: 'warning',
  })
  await deleteMenu(row.id)
  ElMessage.success('删除成功')
  loadData()
}

async function handleSubmit() {
  await formRef.value.validate()
  submitLoading.value = true
  try {
    if (isEdit.value) {
      await updateMenu(form.value)
      ElMessage.success('更新成功')
    } else {
      await createMenu(form.value)
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

.menu-cell {
  display: flex;
  align-items: center;
  gap: 8px;
}

.menu-icon {
  font-size: 16px;
  color: #86868b;
}

.menu-name {
  font-weight: 600;
  color: #1d1d1f;
}

.path-tag {
  background: #f5f5f7;
  padding: 3px 8px;
  border-radius: 6px;
  font-size: 12px;
  font-family: 'SF Mono', 'Fira Code', monospace;
  color: #34c759;
}

.text-muted {
  color: #aeaeb2;
}

.badge {
  display: inline-block;
  padding: 2px 10px;
  border-radius: 6px;
  font-size: 12px;
  font-weight: 600;

  &.badge-success {
    background: #e8f8ed;
    color: #34c759;
  }

  &.badge-default {
    background: #f5f5f7;
    color: #86868b;
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
