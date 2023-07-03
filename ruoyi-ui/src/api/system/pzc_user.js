import request from '@/utils/request'

// 查询用户列表
export function listPzc_user(query) {
  return request({
    url: '/system/pzc_user/list',
    method: 'get',
    params: query
  })
}

// 查询用户详细
export function getPzc_user(userId) {
  return request({
    url: '/system/pzc_user/' + userId,
    method: 'get'
  })
}

// 新增用户
export function addPzc_user(data) {
  return request({
    url: '/system/pzc_user',
    method: 'post',
    data: data
  })
}

// 修改用户
export function updatePzc_user(data) {
  return request({
    url: '/system/pzc_user',
    method: 'put',
    data: data
  })
}

// 删除用户
export function delPzc_user(userId) {
  return request({
    url: '/system/pzc_user/' + userId,
    method: 'delete'
  })
}
