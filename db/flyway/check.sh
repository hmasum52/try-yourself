flyway_changes=$(git diff origin/master HEAD --name-only | grep 'db/flyway/migrations')

if [ -z "$flyway_changes" ]; then
    echo "No Flyway migration files changed."
else
    echo "Flyway migration files changed:"
    echo "$flyway_changes \n"
    changes=$(git diff origin/master HEAD --compact-summary | grep 'db/flyway/migrations')

    # check if there a change is old migrations file
    if [ -z "$(echo "$changes" | grep '-')" ]; then
        echo "No old migrations file changed.\n"
    else
        echo "Old migrations file changed:"
        echo "$(echo "$changes" | grep '-')"
        echo "Please do not modify old migrations files.\n"
        exit 1
    fi

    new_versions=$(echo "$flyway_changes" | grep 'V[0-9]*__' -o)
    # check if there is more than one new migration file
    if [ "$(echo "$new_versions" | wc -l)" -gt 1 ]; then
        echo "More than one new migration file found. Only one new migration file is allowed.\n"
        exit 1
    fi

    new_version=$(echo "$flyway_changes" | grep 'V[0-9]*__' -o | sort -V | head -n 1 | grep '[0-9]*' -o)
    last_migration_version=$(git ls-tree origin/master -r --name-only | grep 'migrations' | grep 'V[0-9]*__' -o | sort -r -V | head -n 1 | grep '[0-9]*' -o)

    if [ -z "$last_migration_version" ]; then
        echo "No old migrations file found.\n"
        # new version should be 1
        if [ "$new_version" -ne 1 ]; then
            echo "New migration file version should be 1.\n"
            exit 1
        fi
    else
        echo "Last migration file version: $last_migration_version\n"
    fi

    if [ -z "$new_version" ]; then
        echo "No new migration file found.\n"
    else
        echo "New migration file version: $new_version\n"
        if [ "$new_version" -le "$last_migration_version" ]; then
            echo "New migration file version is less than or equal to the last migration file version.\n"
            exit 1
        fi

        # difference between new and last migration file version should be 1
        if [ "$((new_version - last_migration_version))" -ne 1 ]; then
            echo "Difference between new and last migration file version should be 1.\n"
            exit 1
        fi
    fi
fi