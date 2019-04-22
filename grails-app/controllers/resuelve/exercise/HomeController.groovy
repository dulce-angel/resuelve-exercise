package resuelve.exercise

import grails.converters.JSON

class HomeController {

    def homeService

    def index() {
        List dataSend = request.JSON

        Map responsePlayers = homeService.initProcess(dataSend)

        response.status = responsePlayers.httpCode
        render([response:[message:responsePlayers.message],
                playersJSON:responsePlayers.playersJSON] as JSON)
    }
}
