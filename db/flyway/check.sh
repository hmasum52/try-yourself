flyway_changes=$(git diff origin/master HEAD --name-only | grep 'db/flyway/migrations')
if [ -z "$flyway_changes" ]; then
    echo "No Flyway migration files changed."
else
    echo "Flyway migration files changed:"
    echo "$flyway_changes \n"
    changes=$(git diff origin/master HEAD --compact-summary | grep 'db/flyway/migrations')

    old_migrations=$(git ls-tree origin/master -r --name-only | grep 'migrations' | grep 'V[0-9]')
    echo $old_migrations

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