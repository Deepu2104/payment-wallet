#!/bin/sh
# Fix Render's DB URL (postgres:// -> jdbc:postgresql://)
if [ -n "$SPRING_DATASOURCE_URL" ]; then
    export SPRING_DATASOURCE_URL=$(echo $SPRING_DATASOURCE_URL | sed 's/^postgres:/jdbc:postgresql:/')
fi

# Run the app
exec java -jar app.jar
