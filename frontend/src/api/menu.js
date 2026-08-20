import request from '@/utils/request'

// 创建菜单
export function createMenu(data) {
  return request.post('/menu/add', data)
}

// 获取所有菜单（平铺）
export function getAllMenus() {
  return request.get('/menu/getAll')
}

// 获取菜单树
export function getMenuTree(data) {
  return request.post('/menu/tree', data)
}

// 获取当前用户的菜单树
export function getUserMenuTree() {
  return request.get('/menu/user/tree')
}

// 根据 ID 获取菜单
export function getMenuById(id) {
  return request.get(`/menu/getById/${id}`)
}

// 更新菜单
export function updateMenu(data) {
  return request.post('/menu/update', data)
}

// 删除菜单
export function deleteMenu(id) {
  return request.post(`/menu/deleteById/${id}`)
}
