package resuelve.exercise

import grails.transaction.Transactional

@Transactional
class HomeService {

    def initProcess(List dataSend){
        List players = getJson(dataSend)

        Map createOrUpdateInfo = createOrUpdateInfo(players)

        if(!createOrUpdateInfo.success){
            return [httpCode: 400,
                    message: createOrUpdateInfo.message,
                    playersJSON:[]]
        }

        List<Player> playerList = createOrUpdateInfo.playerList

        for(int count;count<playerList.size();count++){
            double individualBonus = getIndividualBonus(playerList[count])

            int sumIndGoals = Player.findAllByTeam(playerList[count].team)*.goals.sum()
            int sumMinGoals = Player.findAllByTeam(playerList[count].team)*.mingoals.sum()

            double teamBonus = getTeamBonus(sumIndGoals,sumMinGoals)

            double totalBonus = getTotalBonus(playerList[count].bonus,individualBonus,teamBonus)

            double totalSalary = getTotalSalary(playerList[count].salary,totalBonus)

            updateSalary(playerList[count],totalSalary)
        }

        List playerJSON = returnJson(playerList)

        return [httpCode:200,
                message: 'Proceso de información correcta',
                playersJSON: playerJSON]
    }

    def getJson(List playerJSON){
        List players = []
        Map playersMap

        for(int count;count<playerJSON.size();count++){
            playersMap = [
                    name: playerJSON[count].nombre,
                    level: playerJSON[count].nivel,
                    goals: playerJSON[count].goles,
                    salary: playerJSON[count].sueldo,
                    bonus: playerJSON[count].bono,
                    team: playerJSON[count].equipo,
                    mingoals: playerJSON[count].goles_minimos]
            players.add(playersMap)
        }

        return players
    }

    def createOrUpdateInfo(List players){
        List<Player> playerList = []
        for(int count;count<players.size();count++){
            Player player = Player.findByNameAndTeam(players[count].name,players[count].team)
            int mingoals
            if(players[count].level){
                if(!Level.findByCode(players[count].level)){
                    return [success: false,
                            message: "El nivel ${players[count].level} no se encuentra configurado, " +
                                    "favor de configurar antes de enviar de nuevo"]
                }
                mingoals = Level.findByCode(players[count].level).minGoals
            }else{
                mingoals = players[count].mingoals as int
            }

            if(player){
                player.goals = players[count].goals as int
                player.salary = players[count].salary as double
                player.bonus = players[count].bonus as double
                player.mingoals = mingoals

                player.save(flush: true, failOnError: true)
            }else{
                player = new Player(
                        name: players[count].name,
                        mingoals: mingoals,
                        goals: players[count].goals as int,
                        salary: players[count].salary as double,
                        bonus: players[count].bonus as double,
                        team: players[count].team
                )

                player.save(flush: true, failOnError: true)
            }

            playerList.add(player)
        }

        return [success:true,playerList:playerList]
    }

    def getIndividualBonus(Player player){
        double individualBonus = player.goals / player.mingoals

        return individualBonus
    }

    def getTeamBonus(int sumIndGoals, int sumMinGoals){
        double teamBonus = sumIndGoals / sumMinGoals

        return teamBonus
    }

    def getTotalBonus(double bonus, double individualBonus, double teamBonus){
        double totalBonus = bonus * ((teamBonus/2)+(individualBonus/2))

        return totalBonus
    }

    def getTotalSalary(double salary, double totalBonus){
        double totalSalary = salary + totalBonus

        return totalSalary
    }

    def updateSalary(Player player, double totalSalary){
        player.totalSalary = totalSalary

        player.save(flush: true, failOnError: true)
    }

    def returnJson(List<Player> playersList){
        List playerJSON = []
        Map playerMap = [:]

        playersList.each {
            Level level = Level.findByMinGoals(it.mingoals)
            playerMap = [
                    "nombre":it.name,
                    "nivel":level ? level.code:'',
                    "goles_minimos":it.mingoals,
                    "goles":it.goals,
                    "sueldo":it.salary,
                    "bono":it.bonus,
                    "sueldo_completo":it.totalSalary,
                    "equipo":it.team]

            playerJSON.add(playerMap)
        }

        return playerJSON
    }
}
