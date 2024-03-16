findById
===

* 根据主键查询数据

```graphql
query AccountDepartmentProvider($id: String) {
    organization {
        accounts {
            units {
                departments {
                    findById(id: $id) {
                        id
                        accountId
                        account {
                            id
                            username
                            email
                            mobile
                            name
                        }
                        unitId
                        unit {
                            id
                            areaId
                            parentId
                            code
                            name
                            order
                        }
                        departmentId
                        department {
                            id
                            unitId
                            code
                            name
                            order
                        }
                        postId
                        post {
                            id
                            unitId
                            code
                            name
                            order
                        }
                        primary

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
```

findByIds
===

* 根据主键查询数据

```graphql
query AccountDepartmentProvider($ids: [String]) {
    organization {
        accounts {
            units {
                departments {
                    findByIds(ids: $ids) {
                        id
                        accountId
                        account {
                            id
                            username
                            email
                            mobile
                            name
                        }
                        unitId
                        unit {
                            id
                            areaId
                            parentId
                            code
                            name
                            order
                        }
                        departmentId
                        department {
                            id
                            unitId
                            code
                            name
                            order
                        }
                        postId
                        post {
                            id
                            unitId
                            code
                            name
                            order
                        }
                        primary

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
```

findBy
===

* 根据条件查询数据

```graphql
query AccountDepartmentProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        accounts {
            units {
                departments {
                    findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                        id
                        accountId
                        account {
                            id
                            username
                            email
                            mobile
                            name
                        }
                        unitId
                        unit {
                            id
                            areaId
                            parentId
                            code
                            name
                            order
                        }
                        departmentId
                        department {
                            id
                            unitId
                            code
                            name
                            order
                        }
                        postId
                        post {
                            id
                            unitId
                            code
                            name
                            order
                        }
                        primary

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
```

pageBy
===

* 分页查询数据

```graphql
query AccountDepartmentProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        accounts {
            units {
                departments {
                    pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                        pager {
                            pageIndex
                            pageSize
                            pageCount
                            itemCount
                        }
                        data {
                            ... on AccountDepartment {
                                id
                                accountId
                                account {
                                    id
                                    username
                                    email
                                    mobile
                                    name
                                }
                                unitId
                                unit {
                                    id
                                    areaId
                                    parentId
                                    code
                                    name
                                    order
                                }
                                departmentId
                                department {
                                    id
                                    unitId
                                    code
                                    name
                                    order
                                }
                                postId
                                post {
                                    id
                                    unitId
                                    code
                                    name
                                    order
                                }
                                primary

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
}
```

countBy
===

* 查询符合条件的数据数量

```graphql
query AccountDepartmentProvider($conditions: [ConditionInput]) {
    organization {
        accounts {
            units {
                departments {
                    countBy(conditions: $conditions)
                }
            }
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation AccountDepartmentProvider($input: AccountDepartmentInput, $operator: String) {
    organization {
        accounts {
            units {
                departments {
                    insert(input: $input, operator: $operator) {
                        id
                        accountId
                        account {
                            id
                            username
                            email
                            mobile
                            name
                        }
                        unitId
                        unit {
                            id
                            areaId
                            parentId
                            code
                            name
                            order
                        }
                        departmentId
                        department {
                            id
                            unitId
                            code
                            name
                            order
                        }
                        postId
                        post {
                            id
                            unitId
                            code
                            name
                            order
                        }
                        primary

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
```

insertBatch
===

* 批量保存数据

```graphql
mutation AccountDepartmentProvider($inputs: [AccountDepartmentInput], $operator: String) {
    organization {
        accounts {
            units {
                departments {
                    insertBatch(inputs: $inputs, operator: $operator) {
                        id
                        accountId
                        account {
                            id
                            username
                            email
                            mobile
                            name
                        }
                        unitId
                        unit {
                            id
                            areaId
                            parentId
                            code
                            name
                            order
                        }
                        departmentId
                        department {
                            id
                            unitId
                            code
                            name
                            order
                        }
                        postId
                        post {
                            id
                            unitId
                            code
                            name
                            order
                        }
                        primary

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
```

deleteByIds
===

* 删除数据

```graphql
mutation AccountDepartmentProvider($ids: [String]) {
    organization {
        accounts {
            units {
                departments {
                    deleteByIds(ids: $ids)
                }
            }
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation AccountDepartmentProvider($conditions: [ConditionInput]) {
    organization {
        accounts {
            units {
                departments {
                    deleteBy(conditions: $conditions)
                }
            }
        }
    }
}
```