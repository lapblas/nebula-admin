import request from '@/utils/request'

// 创建用户
export function createUser(data) {
  return request.post('/user/add', data)
}

// 创建管理员
export function createAdminUser(data) {
  return request.post('/user/addAdmin', data)
}

// 获取所有用户
export function getAllUsers() {
  return request.get('/user/getAll')
}

// 分页获取用户
export function getUserPage(data) {
  return request.post('/user/page', data)
}

// 根据 ID 获取用户
export function getUserById(id) {
  return request.get(`/user/getById/${id}`)
}

// 根据用户名获取用户
export function getUserByUsername(username) {
  return request.get(`/user/getByUsername/${username}`)
}

// 根据手机号获取用户
export function getUserByPhone(phone) {
  return request.get(`/user/getByPhone/${phone}`)
}

// 更新用户
export function updateUser(data) {
  return request.post('/user/update', data)
}

// 更新管理员
export function updateAdminUser(data) {
  return request.post('/user/updateAdmin', data)
}

// 删除用户
export function deleteUser(id) {
  return request.post(`/user/deleteById/${id}`)
}

// 删除管理员
export function deleteAdminUser(id) {
  return request.post(`/user/deleteAdmin/${id}`)
}

// 分配角色
export function assignRoles(userId, roleIds) {
  return request.post(`/user/${userId}/roles`, roleIds)
}

// 移除角色
export function removeRoles(userId, roleIds) {
  return request.delete(`/user/${userId}/roles`, { data: roleIds })
}

// 获取用户角色
export function getUserRoles(userId) {
  return request.get(`/user/${userId}/roles`)
}

// 获取用户权限
export function getUserPermissions(userId) {
  return request.get(`/user/${userId}/permissions`)
}
