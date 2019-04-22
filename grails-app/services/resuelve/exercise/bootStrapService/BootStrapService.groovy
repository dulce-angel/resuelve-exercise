package resuelve.exercise.bootStrapService

import grails.transaction.Transactional
import resuelve.exercise.Level

@Transactional
class BootStrapService {

    def insertInitData() {
        if(!Level.count()){
            new Level(
                    code: 'A',
                    minGoals: 5
            ).save(flush: true, failOnError: true)
            new Level(
                    code: 'B',
                    minGoals: 10
            ).save(flush: true, failOnError: true)
            new Level(
                    code: 'C',
                    minGoals: 15
            ).save(flush: true, failOnError: true)
            new Level(
                    code: 'Cuauh',
                    minGoals: 20
            ).save(flush: true, failOnError: true)
        }
    }
}
