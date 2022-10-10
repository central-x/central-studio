findById
===

* 根据主键查询数据

```graphql
query DictionaryProvider($id: String) {
    sys {
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
                    id
                    dictionaryId
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
    sys {
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
                    id
                    dictionaryId
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
    sys {
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
                    id
                    dictionaryId
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
    sys {
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
                            id
                            dictionaryId
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
    sys {
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
    sys {
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
                    id
                    dictionaryId
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
    sys {
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
                    id
                    dictionaryId
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
    sys {
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
                    id
                    dictionaryId
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
    sys {
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
                    id
                    dictionaryId
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
    sys {
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
    sys {
        dictionaries {
            deleteBy(conditions: $conditions)
        }
    }
}
```