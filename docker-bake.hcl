############################################################################
# Global inheritable target
############################################################################
group "default" {
    targets = [
        "central-gateway",
        "central-dashboard",
        "central-security",
        "central-provider",
        "central-logging",
        "central-storage",
        "central-multicast",
    ]
}

############################################################################
# Global inheritable target
############################################################################
target "_platforms" {
    platforms = [
        "linux/arm64",
        "linux/amd64"
    ]
}

variable "STUDIO_VERSION" {
    default = "1.0.x-SNAPSHOT"
}

############################################################################
# Central Studio
############################################################################
target "central-gateway" {
    inherits = ["_platforms"]
    context = "./central-gateway/target"
    dockerfile = "../Dockerfile"
    args = {
        STUDIO_VERSION = "${STUDIO_VERSION}"
    }
    tags = [
        "docker.io/centralx/central-gateway:${STUDIO_VERSION}"
    ]
}

target "central-dashboard" {
     inherits = ["_platforms"]
     context = "./central-dashboard/target"
     dockerfile = "../Dockerfile"
     args = {
         STUDIO_VERSION = "${STUDIO_VERSION}"
     }
     tags = [
         "docker.io/centralx/central-dashboard:${STUDIO_VERSION}"
     ]
}

target "central-security" {
     inherits = ["_platforms"]
     context = "./central-security/target"
     dockerfile = "../Dockerfile"
     args = {
         STUDIO_VERSION = "${STUDIO_VERSION}"
     }
     tags = [
         "docker.io/centralx/central-security:${STUDIO_VERSION}"
     ]
}

target "central-provider" {
    inherits = ["_platforms"]
    context = "./central-provider/target"
    dockerfile = "../Dockerfile"
    args = {
        STUDIO_VERSION = "${STUDIO_VERSION}"
    }
    tags = [
        "docker.io/centralx/central-provider:${STUDIO_VERSION}"
    ]
}

target "central-logging" {
    inherits = ["_platforms"]
    context = "./central-logging/target"
    dockerfile = "../Dockerfile"
    args = {
        STUDIO_VERSION = "${STUDIO_VERSION}"
    }
    tags = [
        "docker.io/centralx/central-logging:${STUDIO_VERSION}"
    ]
}

target "central-logging" {
    inherits = ["_platforms"]
    context = "./central-logging/target"
    dockerfile = "../Dockerfile"
    args = {
        STUDIO_VERSION = "${STUDIO_VERSION}"
    }
    tags = [
        "docker.io/centralx/central-logging:${STUDIO_VERSION}"
    ]
}

target "central-storage" {
    inherits = ["_platforms"]
    context = "./central-storage/target"
    dockerfile = "../Dockerfile"
    args = {
        STUDIO_VERSION = "${STUDIO_VERSION}"
    }
    tags = [
        "docker.io/centralx/central-storage:${STUDIO_VERSION}"
    ]
}

target "central-multicast" {
    inherits = ["_platforms"]
    context = "./central-multicast/target"
    dockerfile = "../Dockerfile"
    args = {
        STUDIO_VERSION = "${STUDIO_VERSION}"
    }
    tags = [
        "docker.io/centralx/central-multicast:${STUDIO_VERSION}"
    ]
}