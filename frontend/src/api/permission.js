import request from '@/utils/request'

// 创建权限
export function createPermission(data) {
  return request.post('/permission/add', data)
}

// 获取所有权限
export function getAllPermissions() {
  return request.get('/permission/getAll')
}

// 分页获取权限
export function getPermissionPage(data) {
  return request.post('/permission/page', data)
}

// 根据 ID 获取权限
export function getPermissionById(id) {
  return request.get(`/permission/getById/${id}`)
}

// 更新权限
export function updatePermission(data) {
  return request.post('/permission/update', data)
}

// 删除权限
export function deletePermission(id) {
  return request.post(`/permission/deleteById/${id}`)
}
