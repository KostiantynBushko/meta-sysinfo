# The systemd service is installed by the recipe, but it is not started
# automatically when this layer is added to a build. A product layer that
# wants the boot time inventory can enable it per image with:
#
#     SYSTEMD_AUTO_ENABLE:mcsysteminfo = "enable"
#
# The service can always be started by hand on the target:
#
#     systemctl start mcsysteminfo.service
#
# Drop this append if the images built from this layer should capture the
# inventory on every boot.

SYSTEMD_AUTO_ENABLE:${PN} = "disable"
