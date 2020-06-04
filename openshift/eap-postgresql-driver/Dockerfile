FROM fedora:latest

USER root

# install postgresql jdbc driver
RUN source /etc/os-release \
    && dnf install postgresql-jdbc -y -v \
        --repo mirror-updates \
        --repo mirror-fedora \
        --repofrompath mirror-updates,https://dl.fedoraproject.org/pub/fedora/linux/updates/${VERSION_ID}/Everything/`uname -i`/ \
        --repofrompath mirror-fedora,https://dl.fedoraproject.org/pub/fedora/linux/releases/${VERSION_ID}/Everything/`uname -i`/os/ \
    && dnf clean all \
    && rm -fr /var/cache/yum

# Copy add-on details
COPY install.sh \
     drivers.env \
     /extensions/
COPY modules /extensions/modules/
RUN cp /usr/share/java/postgresql-jdbc.jar /extensions/modules/system/layers/openshift/org/postgresql/main/

LABEL name="PostgreSQL JDBC Driver Extension for JBoss EAP 7"
