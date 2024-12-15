findById
===

* 根据主键查询数据

```graphql
query RolePermissionProvider($id: String) {
    authority {
        roles {
            permissions {
                findById(id: $id) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    roleId
                    role {
                        id
                        code
                        name
                    }
                    permissionId
                    permission{
                        id
                        code
                        name
                    }

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }
                }
            }
        }
    }
}
```

findByIds
===

* 根据主键查询数据

```graphql
query RolePermissionProvider($ids: [String]) {
    authority {
        roles {
            permissions {
                findByIds(ids: $ids) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    roleId
                    role {
                        id
                        code
                        name
                    }
                    permissionId
                    permission{
                        id
                        code
                        name
                    }

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }
                }
            }
        }
    }
}
```

findBy
===

* 根据条件查询数据

```graphql
query RolePermissionProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    authority {
        roles {
            permissions {
                findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    roleId
                    role {
                        id
                        code
                        name
                    }
                    permissionId
                    permission{
                        id
                        code
                        name
                    }

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }
                }
            }
        }
    }
}
```

pageBy
===

* 分页查询数据

```graphql
query RolePermissionProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    authority {
        roles {
            permissions {
                pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                    pager {
                        pageIndex
                        pageSize
                        pageCount
                        itemCount
                    }
                    data {
                        ... on RolePermission {
                            id
                            applicationId
                            application {
                                id
                                code
                                name
                            }
                            roleId
                            role {
                                id
                                code
                                name
                            }
                            permissionId
                            permission{
                                id
                                code
                                name
                            }

                            creatorId
                            createDate
                            creator {
                                id
                                username
                                name
                            }
                        }
                    }
                }
            }
        }
    }
}
```

countBy
===

* 查询符合条件的数据数量

```graphql
query RolePermissionProvider($conditions: [ConditionInput]) {
    authority {
        roles {
            permissions {
                countBy(conditions: $conditions)
            }
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation RolePermissionProvider($input: RolePermissionInput, $operator: String) {
    authority {
        roles {
            permissions {
                insert(input: $input, operator: $operator) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    roleId
                    role {
                        id
                        code
                        name
                    }
                    permissionId
                    permission{
                        id
                        code
                        name
                    }

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }
                }
            }
        }
    }
}
```

insertBatch
===

* 批量保存数据

```graphql
mutation RolePermissionProvider($inputs: [RolePermissionInput], $operator: String) {
    authority {
        roles {
            permissions {
                insertBatch(inputs: $inputs, operator: $operator) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    roleId
                    role {
                        id
                        code
                        name
                    }
                    permissionId
                    permission{
                        id
                        code
                        name
                    }

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }
                }
            }
        }
    }
}
```

deleteByIds
===

* 删除数据

```graphql
mutation RolePermissionProvider($ids: [String]) {
    authority {
        roles {
            permissions {
                deleteByIds(ids: $ids)
            }
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation RolePermissionProvider($conditions: [ConditionInput]) {
    authority {
        roles {
            permissions {
                deleteBy(conditions: $conditions)
            }
        }
    }
}
```