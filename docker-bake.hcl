############################################################################
# CentralX
# https://central-x.com
############################################################################

#***************************************************************************
# Default Group
#***************************************************************************
group "default" {
    targets = [
        "central-studio"
    ]
}

#***************************************************************************
# Global inheritable target
#***************************************************************************
target "_contexts" {
    contexts = {
        image = "docker-image://centralx/spring-runner:17"
    }
}

target "_platforms" {
    platforms = [
        "linux/arm64",
        "linux/amd64"
    ]
}

target "_labels" {
    labels = {
        "org.opencontainers.image.description" = "Central Studio packaged by CentralX"
        "org.opencontainers.image.vendor" = "CentralX"
        "org.opencontainers.image.maintainer" = "Alan Yeh <alan.yeh.cn>"
    }
}

#***************************************************************************
# Global Argument
#***************************************************************************
variable "STUDIO_VERSION" {
    default = "1.0.x-SNAPSHOT"
}

variable "REGISTRY" {
    default = "docker.io"
}

variable "REPOSITORY" {
    default = "centralx"
}

#***************************************************************************
# Targets
#***************************************************************************
target "central-studio" {
    name = "${STUDIO_COMPONENT}"
    matrix = {
        STUDIO_COMPONENT = [
            "central-gateway",
            "central-dashboard",
            "central-identity",
            "central-provider",
            "central-logging",
            "central-storage",
            "central-multicast"
        ]
    }
    inherits = ["_contexts", "_platforms", "_labels"]
    context = "./${STUDIO_COMPONENT}/target"
    dockerfile = "../Dockerfile"
    labels = {
        "org.opencontainers.image.title" = "${STUDIO_COMPONENT}"
        "org.opencontainers.image.version" = "${STUDIO_VERSION}"
    }
    args = {
        STUDIO_COMPONENT = "${STUDIO_COMPONENT}"
        STUDIO_VERSION = "${STUDIO_VERSION}"
    }
    tags = [
        "${REGISTRY}/${REPOSITORY}/${STUDIO_COMPONENT}:${STUDIO_VERSION}"
    ]
}