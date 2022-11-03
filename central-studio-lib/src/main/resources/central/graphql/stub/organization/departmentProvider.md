findById
===

* 根据主键查询数据

```graphql
query DepartmentInput($id: String) {
    organization {
        departments {
            findById(id: $id) {
                id
                unitId
                unit {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    unitId
                    parentId
                    code
                    name
                }
                code
                name
                order
                children {
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
query DepartmentInput($ids: [String]) {
    organization {
        departments {
            findByIds(ids: $ids){
                id
                unitId
                unit {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    unitId
                    parentId
                    code
                    name
                }
                code
                name
                order
                children {
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
query DepartmentInput($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        departments {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                unitId
                unit {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    unitId
                    parentId
                    code
                    name
                }
                code
                name
                order
                children {
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
query DepartmentInput($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    organization {
        departments {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Department {
                        id
                        unitId
                        unit {
                            id
                            code
                            name
                        }
                        parentId
                        parent {
                            id
                            unitId
                            parentId
                            code
                            name
                        }
                        code
                        name
                        order
                        children {
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
query DepartmentInput($conditions: [ConditionInput]) {
    organization {
        departments {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation DepartmentInput($input: DepartmentInput, $operator: String) {
    organization {
        departments {
            insert(input: $input, operator: $operator) {
                id
                unitId
                unit {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    unitId
                    parentId
                    code
                    name
                }
                code
                name
                order
                children {
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
mutation DepartmentInput($inputs: [DepartmentInput], $operator: String) {
    organization {
        departments {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                unitId
                unit {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    unitId
                    parentId
                    code
                    name
                }
                code
                name
                order
                children {
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
mutation DepartmentInput($input: DepartmentInput, $operator: String) {
    organization {
        departments {
            update(input: $input, operator: $operator) {
                id
                unitId
                unit {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    unitId
                    parentId
                    code
                    name
                }
                code
                name
                order
                children {
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
mutation DepartmentInput($inputs: [DepartmentInput], $operator: String) {
    organization {
        departments {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                unitId
                unit {
                    id
                    code
                    name
                }
                parentId
                parent {
                    id
                    unitId
                    parentId
                    code
                    name
                }
                code
                name
                order
                children {
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
mutation DepartmentInput($ids: [String]) {
    organization {
        departments {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation DepartmentInput($conditions: [ConditionInput]) {
    organization {
        departments {
            deleteBy(conditions: $conditions)
        }
    }
}
```