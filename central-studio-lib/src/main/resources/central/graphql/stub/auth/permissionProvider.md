findById
===

* 根据主键查询数据

```graphql
query PermissionProvider($id: String) {
    auth {
        menus {
            permissions {
                findById(id: $id) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    menuId
                    menu {
                        id
                        code
                        name
                    }
                    code
                    name
                    enabled
                    remark

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }

                    modifierId
                    modifyDate
                    modifier {
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
query PermissionProvider($ids: [String]) {
    auth {
        menus {
            permissions {
                findByIds(ids: $ids) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    menuId
                    menu {
                        id
                        code
                        name
                    }
                    code
                    name
                    enabled
                    remark

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }

                    modifierId
                    modifyDate
                    modifier {
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
query PermissionProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    auth {
        menus {
            permissions {
                findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    menuId
                    menu {
                        id
                        code
                        name
                    }
                    code
                    name
                    enabled
                    remark

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }

                    modifierId
                    modifyDate
                    modifier {
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
query PermissionProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    auth {
        menus {
            permissions {
                pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders) {
                    pager {
                        pageIndex
                        pageSize
                        pageCount
                        itemCount
                    }
                    data {
                        ... on Permission {
                            id
                            applicationId
                            application {
                                id
                                code
                                name
                            }
                            menuId
                            menu {
                                id
                                code
                                name
                            }
                            code
                            name
                            enabled
                            remark

                            creatorId
                            createDate
                            creator {
                                id
                                username
                                name
                            }

                            modifierId
                            modifyDate
                            modifier {
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
query PermissionProvider($conditions: [ConditionInput]) {
    auth {
        menus {
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
mutation PermissionProvider($input: PermissionInput, $operator: String) {
    auth {
        menus {
            permissions {
                insert(input: $input, operator: $operator) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    menuId
                    menu {
                        id
                        code
                        name
                    }
                    code
                    name
                    enabled
                    remark

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }

                    modifierId
                    modifyDate
                    modifier {
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
mutation PermissionProvider($inputs: [PermissionInput], $operator: String) {
    auth {
        menus {
            permissions {
                insertBatch(inputs: $inputs, operator: $operator) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    menuId
                    menu {
                        id
                        code
                        name
                    }
                    code
                    name
                    enabled
                    remark

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }

                    modifierId
                    modifyDate
                    modifier {
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


update
===

* 更新数据

```graphql
mutation RoleProvider($input: PermissionInput, $operator: String) {
    auth {
        menus {
            permissions {
                update(input: $input, operator: $operator) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    menuId
                    menu {
                        id
                        code
                        name
                    }
                    code
                    name
                    enabled
                    remark

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }

                    modifierId
                    modifyDate
                    modifier {
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

updateBatch
===

* 批量更新数据

```graphql
mutation RoleProvider($inputs: [PermissionInput], $operator: String) {
    auth {
        menus {
            permissions {
                updateBatch(inputs: $inputs, operator: $operator) {
                    id
                    applicationId
                    application {
                        id
                        code
                        name
                    }
                    menuId
                    menu {
                        id
                        code
                        name
                    }
                    code
                    name
                    enabled
                    remark

                    creatorId
                    createDate
                    creator {
                        id
                        username
                        name
                    }

                    modifierId
                    modifyDate
                    modifier {
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
mutation PermissionProvider($ids: [String]) {
    auth {
        menus {
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
mutation PermissionProvider($conditions: [ConditionInput]) {
    auth {
        menus {
            permissions {
                deleteBy(conditions: $conditions)
            }
        }
    }
}
```