SUMMARY = "System information utility"
DESCRIPTION = "Reports hardware and operating system information of the running \
               system, either as human readable text or as JSON."
HOMEPAGE = "https://github.com/KostiantynBushko/mcsysteminfo"
SECTION = "base"
LICENSE = "MIT"
LIC_FILES_CHKSUM = "file://${COMMON_LICENSE_DIR}/MIT;md5=0835ade698e0bcf8506ecda2f7b4f302"

# The sources are maintained in their own git repository.
SRC_URI = "git://github.com/KostiantynBushko/mcsysteminfo.git;protocol=https;branch=main"
SRCREV = "6d1c62c972f24725d4c355fd76b9d9fcdf1a2198"

# A git checkout is not a tarball, so there is no sha256sum to verify.
SRC_URI[sha256sum] = ""

S = "${WORKDIR}/git"

inherit cmake pkgconfig systemd ptest

# Build the unit test as well, it is shipped in the ${PN}-ptest package.
EXTRA_OECMAKE:append = " -DMCSYSTEMINFO_BUILD_TESTS=ON"

# base-files provides /etc/hostname and /etc/hosts. /etc/machine-id is
# optional, the tool reports an empty value when it is not present.
RDEPENDS:${PN} += "base-files"

# The boot time inventory is captured by the systemd service.
SYSTEMD_SERVICE:${PN} += "mcsysteminfo.service"

do_install:append() {
    install -d ${D}${systemd_unitdir}/system
    install -m 0644 ${THISDIR}/files/mcsysteminfo.service \
        ${D}${systemd_unitdir}/system/mcsysteminfo.service

    # The unit test is cross compiled, therefore it is executed on the target
    # by the image runtime test framework and not on the build host.
    if [ "${PTEST_ENABLED}" = "1" ]; then
        install -d ${D}${PTEST_PATH}
        install -m 0755 ${B}/mcsysteminfo_test ${D}${PTEST_PATH}/mcsysteminfo_test
    fi
}

do_test() {
    if [ "${BUILD_SYS}" = "${HOST_SYS}" ]; then
        ${B}/mcsysteminfo_test
    else
        bbnote "Not running the unit test on the build host, the test binary is cross compiled."
    fi
}

# The runtime package holds the binary and the systemd unit.
FILES:${PN} = "${bindir}/mcsysteminfo \
               ${systemd_unitdir}/system/mcsysteminfo.service"

# The static library is shipped separately, it is only needed by packages
# that link against the tool.
FILES:${PN}-staticdev = "${libdir}/libmcsysteminfo.a"

# The development package holds the headers, the pkg-config file and the
# CMake package config.
FILES:${PN}-dev = "${includedir}/mcsysteminfo/*.hpp \
                   ${libdir}/pkgconfig/mcsysteminfo.pc \
                   ${libdir}/cmake/mcsysteminfo/*"
