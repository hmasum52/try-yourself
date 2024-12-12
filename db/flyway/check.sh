
last_migration_version=$(git ls-tree origin/master -r --name-only | grep 'migrations' | grep '.[0-9]' -o | sort -r | head -n 1 | grep '[0-9]' -o)

if [ -z "$last_migration_version" ]; then
    echo "No old migrations file found."
else
    echo "Last migration file version: $last_migration_version"
fi

flyway_changes=$(git diff origin/master HEAD --name-only | grep 'db/flyway/migrations')

if [ -z "$flyway_changes" ]; then
    echo "No Flyway migration files changed."
else
    echo "Flyway migration files changed:"
    echo "$flyway_changes \n"
    changes=$(git diff origin/master HEAD --compact-summary | grep 'db/flyway/migrations')

    # check if there a change is old migrations file
    if [ -z "$(echo "$changes" | grep '-')" ]; then
        echo "No old migrations file changed."
    else
        echo "Old migrations file changed:"
        echo "$(echo "$changes" | grep '-')"
        echo "Please do not modify old migrations files."
        exit 1
    fi
fi