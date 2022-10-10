findById
===

* 根据主键查询数据

```graphql
query DictionaryItemProvider($id: String) {
    sys {
        dictionaries {
            items {
                findById(id: $id) {
                    id
                    dictionaryId
                    dictionary {
                        id
                        code
                        name
                    }
                    code
                    name
                    primary
                    order

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
query DictionaryItemProvider($ids: [String]) {
    sys {
        dictionaries {
            items {
                findByIds(ids: $ids) {
                    id
                    dictionaryId
                    dictionary {
                        id
                        code
                        name
                    }
                    code
                    name
                    primary
                    order

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
query DictionaryItemProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    sys {
        dictionaries {
            items {
                findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
                    id
                    dictionaryId
                    dictionary {
                        id
                        code
                        name
                    }
                    code
                    name
                    primary
                    order

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
query DictionaryItemProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    sys {
        dictionaries {
            items {
                pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders){
                    pager {
                        pageIndex
                        pageSize
                        pageCount
                        itemCount
                    }
                    data {
                        ... on DictionaryItem {
                            id
                            dictionaryId
                            dictionary {
                                id
                                code
                                name
                            }
                            code
                            name
                            primary
                            order

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
query DictionaryItemProvider($conditions: [ConditionInput]) {
    sys {
        dictionaries {
            items {
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
mutation DictionaryItemProvider($input: DictionaryItemInput, $operator: String) {
    sys {
        dictionaries {
            items {
                insert(input: $input, operator: $operator) {
                    id
                    dictionaryId
                    dictionary {
                        id
                        code
                        name
                    }
                    code
                    name
                    primary
                    order

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
mutation DictionaryItemProvider($inputs: [DictionaryItemInput], $operator: String) {
    sys {
        dictionaries {
            items {
                insertBatch(inputs: $inputs, operator: $operator) {
                    id
                    dictionaryId
                    dictionary {
                        id
                        code
                        name
                    }
                    code
                    name
                    primary
                    order

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
mutation DictionaryItemProvider($input: DictionaryItemInput, $operator: String) {
    sys {
        dictionaries {
            items {
                update(input: $input, operator: $operator) {
                    id
                    dictionaryId
                    dictionary {
                        id
                        code
                        name
                    }
                    code
                    name
                    primary
                    order

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
mutation DictionaryItemProvider($inputs: [DictionaryItemInput], $operator: String) {
    sys {
        dictionaries {
            items {
                updateBatch(inputs: $inputs, operator: $operator) {
                    id
                    dictionaryId
                    dictionary {
                        id
                        code
                        name
                    }
                    code
                    name
                    primary
                    order

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
mutation DictionaryItemProvider($ids: [String]) {
    sys {
        dictionaries {
            items {
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
mutation DictionaryItemProvider($conditions: [ConditionInput]) {
    sys {
        dictionaries {
            items {
                deleteBy(conditions: $conditions)
            }
        }
    }
}
```