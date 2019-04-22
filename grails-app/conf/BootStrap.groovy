class BootStrap {

    def bootStrapService

    def init = { servletContext ->
        bootStrapService.insertInitData()
    }
    def destroy = {
    }
}
