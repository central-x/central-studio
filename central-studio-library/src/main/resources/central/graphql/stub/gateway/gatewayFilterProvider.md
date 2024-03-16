findById
===

* 根据主键查询数据

```graphql
query GatewayFilterProvider($id: String) {
    gateway {
        filters {
            findById(id: $id) {
                id
                type
                path
                order
                enabled
                remark
                params
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
query GatewayFilterProvider($ids: [String]) {
    gateway {
        filters {
            findByIds(ids: $ids){
                id
                type
                path
                order
                enabled
                remark
                params
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
query GatewayFilterProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    gateway {
        filters {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                type
                path
                order
                enabled
                remark
                params
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
query GatewayFilterProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    gateway {
        filters {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on GatewayFilter {
                        id
                        type
                        path
                        order
                        enabled
                        remark
                        params
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
query GatewayFilterProvider($conditions: [ConditionInput]) {
    gateway {
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
mutation GatewayFilterProvider($input: GatewayFilterInput, $operator: String) {
    gateway {
        filters {
            insert(input: $input, operator: $operator) {
                id
                type
                path
                order
                enabled
                remark
                params
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
mutation GatewayFilterProvider($inputs: [GatewayFilterInput], $operator: String) {
    gateway {
        filters {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                type
                path
                order
                enabled
                remark
                params
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
mutation GatewayFilterProvider($input: GatewayFilterInput, $operator: String) {
    gateway {
        filters {
            update(input: $input, operator: $operator) {
                id
                type
                path
                order
                enabled
                remark
                params
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
mutation GatewayFilterProvider($inputs: [GatewayFilterInput], $operator: String) {
    gateway {
        filters {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                type
                path
                order
                enabled
                remark
                params
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
mutation GatewayFilterProvider($ids: [String]) {
    gateway {
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
mutation GatewayFilterProvider($conditions: [ConditionInput]) {
    gateway {
        filters {
            deleteBy(conditions: $conditions)
        }
    }
}
```