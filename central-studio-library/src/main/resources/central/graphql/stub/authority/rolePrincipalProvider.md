findById
===

* 根据主键查询数据

```graphql
query RolePrincipalProvider($id: String) {
    authority {
        roles {
            principals {
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
                    principalId
                    type
                    account {
                        id
                        username
                        name
                    }
                    unit {
                        id
                        name
                    }
                    department {
                        id
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
query RolePrincipalProvider($ids: [String]) {
    authority {
        roles {
            principals {
                findByIds(ids: $ids){
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
                    principalId
                    type
                    account {
                        id
                        username
                        name
                    }
                    unit {
                        id
                        name
                    }
                    department {
                        id
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
query RolePrincipalProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    authority {
        roles {
            principals {
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
                    principalId
                    type
                    account {
                        id
                        username
                        name
                    }
                    unit {
                        id
                        name
                    }
                    department {
                        id
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
query RolePrincipalProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    authority {
        roles {
            principals {
                pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                    pager {
                        pageIndex
                        pageSize
                        pageCount
                        itemCount
                    }
                    data {
                        ... on RolePrincipal {
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
                            principalId
                            type
                            account {
                                id
                                username
                                name
                            }
                            unit {
                                id
                                name
                            }
                            department {
                                id
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
query RolePrincipalProvider($conditions: [ConditionInput]) {
    authority {
        roles {
            principals {
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
mutation RolePrincipalProvider($input: RolePrincipalInput, $operator: String) {
    authority {
        roles {
            principals {
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
                    principalId
                    type
                    account {
                        id
                        username
                        name
                    }
                    unit {
                        id
                        name
                    }
                    department {
                        id
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
mutation RolePrincipalProvider($inputs: [RolePrincipalInput], $operator: String) {
    authority {
        roles {
            principals {
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
                    principalId
                    type
                    account {
                        id
                        username
                        name
                    }
                    unit {
                        id
                        name
                    }
                    department {
                        id
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
mutation RolePrincipalProvider($ids: [String]) {
    authority {
        roles {
            principals {
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
mutation RolePrincipalProvider($conditions: [ConditionInput]) {
    authority {
        roles {
            principals {
                deleteBy(conditions: $conditions)
            }
        }
    }
}
```