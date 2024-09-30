findById
===

* 根据主键查询数据

```graphql
query DictionaryProvider($id: String) {
    system {
        dictionaries {
            findById(id: $id) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                enabled
                remark
                
                items {
                    code
                    name
                    primary
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
query DictionaryProvider($ids: [String]) {
    system {
        dictionaries {
            findByIds(ids: $ids){
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                enabled
                remark

                items {
                    code
                    name
                    primary
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
query DictionaryProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    system {
        dictionaries {
            findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                enabled
                remark

                items {
                    code
                    name
                    primary
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
query DictionaryProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    system {
        dictionaries {
            pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                pager {
                    pageIndex
                    pageSize
                    pageCount
                    itemCount
                }
                data {
                    ... on Dictionary {
                        id
                        applicationId
                        application {
                            id
                            code
                            name
                        }
                        code
                        name
                        enabled
                        remark

                        items {
                            code
                            name
                            primary
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
query DictionaryProvider($conditions: [ConditionInput]) {
    system {
        dictionaries {
            countBy(conditions: $conditions)
        }
    }
}
```

insert
===

* 保存数据

```graphql
mutation DictionaryProvider($input: DictionaryInput, $operator: String) {
    system {
        dictionaries {
            insert(input: $input, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                enabled
                remark

                items {
                    code
                    name
                    primary
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
mutation DictionaryProvider($inputs: [DictionaryInput], $operator: String) {
    system {
        dictionaries {
            insertBatch(inputs: $inputs, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                enabled
                remark

                items {
                    code
                    name
                    primary
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
mutation DictionaryProvider($input: DictionaryInput, $operator: String) {
    system {
        dictionaries {
            update(input: $input, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                enabled
                remark

                items {
                    code
                    name
                    primary
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
mutation DictionaryProvider($inputs: [DictionaryInput], $operator: String) {
    system {
        dictionaries {
            updateBatch(inputs: $inputs, operator: $operator) {
                id
                applicationId
                application {
                    id
                    code
                    name
                }
                code
                name
                enabled
                remark

                items {
                    code
                    name
                    primary
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
mutation DictionaryProvider($ids: [String]) {
    system {
        dictionaries {
            deleteByIds(ids: $ids)
        }
    }
}
```

deleteBy
===

* 删除数据

```graphql
mutation DictionaryProvider($conditions: [ConditionInput]) {
    system {
        dictionaries {
            deleteBy(conditions: $conditions)
        }
    }
}
```