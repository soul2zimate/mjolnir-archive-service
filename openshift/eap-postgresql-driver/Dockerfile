FROM scratch

# Labels
LABEL name="PostgreSQL JDBC Driver" \
      version="42.2.11"

# Copy add-on details
COPY install.sh \
     drivers.env \
     /extensions/
COPY modules /extensions/modules/

# Download the driver into the module folder
ADD https://jdbc.postgresql.org/download/postgresql-42.2.11.jar \
    /extensions/modules/system/layers/openshift/org/postgresql/main/