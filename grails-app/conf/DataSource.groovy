hibernate {
    cache.use_second_level_cache = true
    cache.use_query_cache = false
//    cache.region.factory_class = 'org.hibernate.cache.SingletonEhCacheRegionFactory' // Hibernate 3
    cache.region.factory_class = 'org.hibernate.cache.ehcache.SingletonEhCacheRegionFactory' // Hibernate 4
    singleSession = true // configure OSIV singleSession mode
    flush.mode = 'manual' // OSIV session flush mode outside of transactional context
}

// environment specific settings
environments {
    development {
        dataSource {
            pooled = true
            driverClassName = "com.mysql.jdbc.Driver"
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect
            username = "root"
            password = "root"
            dbCreate = "update" // one of 'create', 'create-drop', 'update', 'validate', ''
            url = "jdbc:mysql://localhost:3306/ResuelveExercise"
        }
    }
    test {
        dataSource {
            pooled = false
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect

            dbCreate = "update"
            jndiName = "java:comp/env/jdbc/ResuelveExercise_test"
        }
    }
    production {
        dataSource {
            pooled = false
            dialect = org.hibernate.dialect.MySQL5InnoDBDialect

            dbCreate = "update"
            jndiName = "java:comp/env/jdbc/ResuelveExercise"
        }
    }
}
