findById
===

* 根据主键查询数据

```graphql
query LogFilterProvider($id: String) {
    log {
        filters {
            findById(id: $id) {
                id
                code
                name
                enabled
                remark
                collectors {
                    id
                    code
                    name
                    enabled
                }
                storages {
                    id
                    code
                    name
                    enabled
                }
                predicates {
                    type
                    params
                }

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
query LogFilterProvider($ids: [String]) {
    log {
        filters {
            findByIds(ids: $ids){
                id
                code
                name
                enabled
                remark
                collectors {
                    id
                    code
                    name
                    enabled
                }
                storages {
                    id
                    code
                    name
                    enabled
                }
                predicates {
                    type
                    params
                }

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
query LogFilterProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    log {
        filters {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                code
                name
                enabled
                remark
                collectors {
                    id
                    code
                    name
                    enabled
                }
                storages {
                    id
                    code
                    name
                    enabled
                }
                predicates {
                    type
                    params
                }

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
query LogFilterProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    log {
        filters {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on LogFilter {
                        id
                        code
                        name
                        enabled
                        remark
                        collectors {
                            id
                            code
                            name
                            enabled
                        }
                        storages {
                            id
                            code
                            name
                            enabled
                        }
                        predicates {
                            type
                            params
                        }

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
query LogFilterProvider($conditions: [ConditionInput]) {
    log {
        filters {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation LogFilterProvider($input: LogFilterInput, $operator: String) {
    log {
        filters {
            insert(input: $input, operator: $operator) {
                id
                code
                name
                enabled
                remark
                collectors {
                    id
                    code
                    name
                    enabled
                }
                storages {
                    id
                    code
                    name
                    enabled
                }
                predicates {
                    type
                    params
                }

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
mutation LogFilterProvider($inputs: [LogFilterInput], $operator: String) {
    log {
        filters {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                code
                name
                enabled
                remark
                collectors {
                    id
                    code
                    name
                    enabled
                }
                storages {
                    id
                    code
                    name
                    enabled
                }
                predicates {
                    type
                    params
                }

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
mutation LogFilterProvider($input: LogFilterInput, $operator: String) {
    log {
        filters {
            update(input: $input, operator: $operator) {
                id
                code
                name
                enabled
                remark
                collectors {
                    id
                    code
                    name
                    enabled
                }
                storages {
                    id
                    code
                    name
                    enabled
                }
                predicates {
                    type
                    params
                }

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
mutation LogFilterProvider($inputs: [LogFilterInput], $operator: String) {
    log {
        filters {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                code
                name
                enabled
                remark
                collectors {
                    id
                    code
                    name
                    enabled
                }
                storages {
                    id
                    code
                    name
                    enabled
                }
                predicates {
                    type
                    params
                }

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
mutation LogFilterProvider($ids: [String]) {
    log {
        filters {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation LogFilterProvider($conditions: [ConditionInput]) {
    log {
        filters {
            deleteBy(conditions: $conditions)
        }
    }
}
```