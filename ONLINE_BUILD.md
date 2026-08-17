# Online build — NZ Scanner v0.2

This project includes a GitHub Actions workflow for building the debug APK without Android Studio.

## GitHub

1. Create a new GitHub repository.
2. Upload all files from this folder.
3. Push to the `main` branch.
4. Open the repository's **Actions** tab.
5. Select **Build NZ Scanner APK**.
6. Click **Run workflow**.
7. When it finishes, open the workflow run and download the `nz-scanner-debug-apk` artifact.
8. Extract the artifact to obtain `app-debug.apk`.

The workflow installs the Android 36 SDK and uses JDK 17 + Gradle 8.13.

## Important

The project intentionally contains placeholder stream URLs. Replace them only with public feeds you are authorized to redistribute.

This build does not contain Police encrypted-network interception/decryption functionality.
