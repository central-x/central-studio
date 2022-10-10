findById
===

* 根据主键查询数据

```graphql
query UnitProvider($id: String) {
    org {
        units {
            findById(id: $id) {
                id
                areaId
                area {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    areaId
                    parentId
                    code
                    name
                }
                children {
                    id
                    code
                    name
                    order
                }
                code
                name
                order
                
                departments {
                    id
                    code
                    name
                    order
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
query UnitProvider($ids: [String]) {
    org {
        units {
            findByIds(ids: $ids){
                id
                areaId
                area {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    areaId
                    parentId
                    code
                    name
                }
                children {
                    id
                    code
                    name
                    order
                }
                code
                name
                order

                departments {
                    id
                    code
                    name
                    order
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
query UnitProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    org {
        units {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                areaId
                area {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    areaId
                    parentId
                    code
                    name
                }
                children {
                    id
                    code
                    name
                    order
                }
                code
                name
                order

                departments {
                    id
                    code
                    name
                    order
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
query UnitProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    org {
        units {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Unit {
                        id
                        areaId
                        area {
                            id
                            code
                            name
                        }
                        parentId
                        parent {
                            id
                            areaId
                            parentId
                            code
                            name
                        }
                        children {
                            id
                            code
                            name
                            order
                        }
                        code
                        name
                        order

                        departments {
                            id
                            code
                            name
                            order
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
query UnitProvider($conditions: [ConditionInput]) {
    org {
        units {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation UnitProvider($input: UnitInput, $operator: String) {
    org {
        units {
            insert(input: $input, operator: $operator) {
                id
                areaId
                area {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    areaId
                    parentId
                    code
                    name
                }
                children {
                    id
                    code
                    name
                    order
                }
                code
                name
                order

                departments {
                    id
                    code
                    name
                    order
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
mutation UnitProvider($inputs: [UnitInput], $operator: String) {
    org {
        units {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                areaId
                area {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    areaId
                    parentId
                    code
                    name
                }
                children {
                    id
                    code
                    name
                    order
                }
                code
                name
                order

                departments {
                    id
                    code
                    name
                    order
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
mutation UnitProvider($input: UnitInput, $operator: String) {
    org {
        units {
            update(input: $input, operator: $operator) {
                id
                areaId
                area {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    areaId
                    parentId
                    code
                    name
                }
                children {
                    id
                    code
                    name
                    order
                }
                code
                name
                order

                departments {
                    id
                    code
                    name
                    order
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
mutation UnitProvider($inputs: [UnitInput], $operator: String) {
    org {
        units {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                areaId
                area {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    areaId
                    parentId
                    code
                    name
                }
                children {
                    id
                    code
                    name
                    order
                }
                code
                name
                order

                departments {
                    id
                    code
                    name
                    order
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
mutation UnitProvider($ids: [String]) {
    org {
        units {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation UnitProvider($conditions: [ConditionInput]) {
    org {
        units {
            deleteBy(conditions: $conditions)
        }
    }
}
```