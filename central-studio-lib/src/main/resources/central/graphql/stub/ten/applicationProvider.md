findById
===

* 查询数据

```graphql
query ApplicationProvider($id: String) {
    result: Application_findById(id: $id) {
        id
        code
        name
        logo
        url
        contextPath
        key
        enabled
        remark

        modules {
            id
            applicationId
            url
            contextPath
            enabled
            remark
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
```

findModuleById
===

* 查询权限数据

```graphql
query ApplicationProvider($id: String) {
    result: Application_findModuleById(id: $id) {
        id
        applicationId
        application {
            id
            code
            name
            url
            contextPath
            key
            enabled
            remark
        }
        url
        contextPath
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
```

findByIds
===

* 查询数据

```graphql
query ApplicationProvider($ids: [String]) {
    result: Application_findByIds(ids: $ids) {
        id
        code
        name
        logo
        url
        contextPath
        key
        enabled
        remark

        modules {
            id
            applicationId
            url
            contextPath
            enabled
            remark
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
```

findModuleByIds
===

* 查询权限数据

```graphql
query ApplicationProvider($ids: [String]) {
    result: Application_findModuleByIds(ids: $ids) {
        id
        applicationId
        application {
            id
            code
            name
            url
            contextPath
            key
            enabled
            remark
        }
        url
        contextPath
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
```

findBy
===

* 查询数据

```graphql
query ApplicationProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    result: Application_findBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
        id
        code
        name
        logo
        url
        contextPath
        key
        enabled
        remark

        modules {
            id
            applicationId
            url
            contextPath
            enabled
            remark
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
```

findModuleBy
===

* 查询权限数据

```graphql
query ApplicationProvider($limit: Long, $offset: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    result: Application_findModuleBy(limit: $limit, offset: $offset, conditions: $conditions, orders: $orders) {
        id
        applicationId
        application {
            id
            code
            name
            url
            contextPath
            key
            enabled
            remark
        }
        url
        contextPath
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
```

pageBy
===

* 分页查询数据

```graphql
query ApplicationProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    result: Application_pageBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders) {
        pager {
            pageIndex
            pageSize
            pageCount
            itemCount
        }
        data {
            ... on Application {
                id
                code
                name
                logo
                url
                contextPath
                key
                enabled
                remark

                modules {
                    id
                    applicationId
                    url
                    contextPath
                    enabled
                    remark
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

pageModuleBy
===

* 分页查询权限数据

```graphql
query ApplicationProvider($pageIndex: Long, $pageSize: Long, $conditions: [ConditionInput], $orders: [OrderInput]) {
    result: Application_pageModuleBy(pageIndex: $pageIndex, pageSize: $pageSize, conditions: $conditions, orders: $orders) {
        pager {
            pageIndex
            pageSize
            pageCount
            itemCount
        }
        data {
            ... on ApplicationModule {
                id
                applicationId
                application {
                    id
                    code
                    name
                    url
                    contextPath
                    key
                    enabled
                    remark
                }
                url
                contextPath
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

countBy
===

* 查询符合条件的数据数量

```graphql
query ApplicationProvider($conditions: [ConditionInput]) {
    result: Application_countBy(conditions: $conditions)
}
```

countModuleBy
===

* 查询符合条件的数据数量

```graphql
query ApplicationProvider($conditions: [ConditionInput]) {
    result: Application_countModuleBy(conditions: $conditions)
}
```

insert
===

* 保存数据

```graphql
mutation ApplicationProvider($input: ApplicationInput, $operator: String) {
    result: Application_insert(input: $input, operator: $operator) {
        id
        code
        name
        logo
        url
        contextPath
        key
        enabled
        remark

        modules {
            id
            applicationId
            url
            contextPath
            enabled
            remark
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
```

insertModule
===

* 保存权限数据

```graphql
mutation ApplicationProvider($input: ApplicationModuleInput, $operator: String) {
    result: Application_insertModule(input: $input, operator: $operator) {
        id
        applicationId
        application {
            id
            code
            name
            url
            contextPath
            key
            enabled
            remark
        }
        url
        contextPath
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
```

update
===

* 更新数据

```graphql
mutation ApplicationProvider($input: ApplicationInput, $operator: String) {
    result: Application_update(input: $input, operator: $operator) {
        id
        code
        name
        logo
        url
        contextPath
        key
        enabled
        remark

        modules {
            id
            applicationId
            url
            contextPath
            enabled
            remark
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
```

updateModule
===

* 更新数据

```graphql
mutation ApplicationProvider($input: ApplicationModuleInput, $operator: String) {
    result: Application_updateModule(input: $input, operator: $operator) {
        id
        applicationId
        application {
            id
            code
            name
            url
            contextPath
            key
            enabled
            remark
        }
        url
        contextPath
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
```

deleteByIds
===

* 删除数据

```graphql
mutation ApplicationProvider($ids: [String]) {
    result: Application_deleteByIds(ids: $ids)
}
```

deleteApplicationModuleByIds
===

* 删除权限数据

```graphql
mutation ApplicationProvider($ids: [String]) {
    result: Application_deleteModuleByIds(ids: $ids)
}
```