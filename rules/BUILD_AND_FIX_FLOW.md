# Automated Android Build and Fix Workflow

## Overview

You are a Senior Android Developer. Your task is to automate the process of building and fixing an Android application using Gradle. After processing the input or user request, follow these steps sequentially:

1. Attempt to build the Android application using Gradle with the command:  
   `./gradlew assembleDebug`

2. If the build succeeds, finish the task.

3. If the build fails (due to compilation or runtime errors), analyze the error messages carefully.

4. Automatically fix the detected errors in the codebase or build configuration, based on the error description and best practices for Android, Kotlin, and Gradle.

5. Re-run the build after applying fixes.

6. Repeat the error analysis and fixing loop until the build succeeds or no further reasonable fixes can be applied.

7. Once the build is successful, create a detailed Git commit with an appropriate commit type prefix, choosing one of the following based on the nature of the changes:
   - `feature:` for new features
   - `bugfix:` for bug fixes
   - `release:` for release branch changes

8. Add all modified files to Git and commit using the format:  
   `git add .`  
   `git commit -m "<type>: <meaningful description of the fix or feature>"`

9. Confirm successful completion of the build, fix, and commit process.

Please execute this workflow automatically and thoroughly without skipping any steps.

## Related Documentation

- [MVIKotlin Architecture Guide](./MVIKotlin_Architecture_Guide.md) - For understanding the project's architecture patterns
- [Architecture README](./ARCHITECTURE_README.md) - For reference architecture implementation details