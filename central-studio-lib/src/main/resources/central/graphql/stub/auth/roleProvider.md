findById
===

* 根据主键查询数据

```graphql
query RoleProvider($id: String) {
    auth {
        roles {
            findById(id: $id) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                unitId
                unit {
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
```

findByIds
===

* 根据主键查询数据

```graphql
query RoleProvider($ids: [String]) {
    auth {
        roles {
            findByIds(ids: $ids) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                unitId
                unit {
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
```

findBy
===

* 根据条件查询数据

```graphql
query RoleProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    auth {
        roles {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                unitId
                unit {
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
```

pageBy
===

* 分页查询数据

```graphql
query RoleProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    auth {
        roles {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Role {
                        id
                        applicationId
                        application {
                            id
                            code
                            name
                        }
                        unitId
                        unit {
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
```

countBy
===

* 查询符合条件的数据数量

```graphql
query RoleProvider($conditions: [ConditionInput]) {
    auth {
        roles {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation RoleProvider($input: RoleInput, $operator: String) {
    auth {
        roles {
            insert(input: $input, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                unitId
                unit {
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
```

insertBatch
===

* 批量保存数据

```graphql
mutation RoleProvider($inputs: [RoleInput], $operator: String) {
    auth {
        roles {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                unitId
                unit {
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
```

update
===

* 更新数据

```graphql
mutation RoleProvider($input: RoleInput, $operator: String) {
    auth {
        roles {
            update(input: $input, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                unitId
                unit {
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
```

updateBatch
===

* 批量更新数据

```graphql
mutation RoleProvider($inputs: [RoleInput], $operator: String) {
    auth {
        roles {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                unitId
                unit {
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
```

deleteByIds
===

* 删除数据

```graphql
mutation RoleProvider($ids: [String]) {
    auth {
        roles {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation RoleProvider($conditions: [ConditionInput]) {
    auth {
        roles {
            deleteBy(conditions: $conditions)
        }
    }
}
```