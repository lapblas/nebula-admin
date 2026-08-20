import request from '@/utils/request'

// 创建角色
export function createRole(data) {
  return request.post('/role/add', data)
}

// 获取所有角色
export function getAllRoles() {
  return request.get('/role/getAll')
}

// 分页获取角色
export function getRolePage(data) {
  return request.post('/role/page', data)
}

// 根据 ID 获取角色
export function getRoleById(id) {
  return request.get(`/role/getById/${id}`)
}

// 更新角色
export function updateRole(data) {
  return request.post('/role/update', data)
}

// 删除角色
export function deleteRole(id) {
  return request.post(`/role/deleteById/${id}`)
}
