flyway_changes=$(git diff origin/master HEAD --name-only | grep 'db/flyway/migrations')
if [ -z "$flyway_changes" ]; then
    echo "No Flyway migration files changed."
else
    echo "Flyway migration files changed:"
    echo "$flyway_changes"
    changes=$(git diff origin/master HEAD --compact-summary | grep 'db/flyway/migrations')
    echo "$changes"
fi