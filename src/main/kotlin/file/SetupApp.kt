package file

import global.InstallationManager
import mu.KotlinLogging
import ui.UiManager
import ui.UpdateFoundDialog
import java.nio.file.Files
import java.nio.file.Paths
import java.nio.file.StandardCopyOption


object SetupApp {
    private val log = KotlinLogging.logger {}
    lateinit var setup: SetupMain

    fun handleArgs(setup: SetupMain) {
        this.setup = setup
        if (!setup.silent) {
            log.info("is GUI launch")
            UiManager.initUI()
        } else {
            log.info("is silent launch")
            handleCMD()
        }
        startup()
    }

    private fun handleCMD() {
        if (setup.removeInstallation) {
            log.info("remove installation")
            if (setup.force) {
                InstallationManager.handleRemove()
            }
        } else if (setup.updateInstall) {
            if (InstallationManager.status == InstallationManager.InstallationStatus.INSTALLED_OUTDATED
                    || InstallationManager.status == InstallationManager.InstallationStatus.NOT_INSTALLED) {
                log.info("compiler update found")
                if (setup.force) {
                    InstallationManager.handleUpdate()
                } else {
                    UpdateFoundDialog("A Wurst compiler update has been found!")
                }
            }
        } else if (setup.createProject) {
            log.info("is create project")
            if (setup.projectDir != null) {
                log.info("project dir exists")
                WurstProjectConfig.handleCreate(setup.projectDir!!, null, WurstProjectConfigData())
            }
        } else if (setup.updateProject) {
            log.info("is update project")
            if (setup.projectDir != null) {
                log.info("project dir exists")
                WurstProjectConfig.handleUpdate(setup.projectDir!!, null, WurstProjectConfig.loadProject(setup.projectDir!!.resolve("wurst.build"))!!)
            }
        }
    }

    private fun startup() {
        log.info("startup setup version: <{}>", CompileTimeInfo.version)
//        val conStatus = ConnectionManager.checkConnectivity()
//        log.info("ConnectionStatus: $conStatus")
//        when (conStatus) {
//            NetStatus.CLIENT_OFFLINE -> Log.print("Client offline. All update functionality disabled.")
//            NetStatus.SERVER_OFFLINE -> Log.print("Server offline. All update functionality disabled.")
//            NetStatus.ONLINE -> Log.println("Server online!")
//        }

        InstallationManager.verifyInstallation()
        copyJar()
    }

    private fun copyJar() {
        val url = InstallationManager::class.java.protectionDomain.codeSource.location
        val ownFile = Paths.get(url.toURI())
        log.info("path: $url")
        log.info("file: " + ownFile.toAbsolutePath())
        if (ownFile != null && Files.exists(ownFile) && ownFile.toString().endsWith(".jar") &&
                (ownFile.parent == null || ownFile.parent.fileName.toString() != ".wurst")) {
            log.info("copy jar")
            Files.copy(ownFile, Paths.get(InstallationManager.installDir.toString(), "WurstSetup.jar"), StandardCopyOption.REPLACE_EXISTING)
        }
    }
}
