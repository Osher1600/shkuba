# Shkuba Android App

An Android application built with Kotlin and Jetpack Compose.

## CI/CD Pipeline

This project uses GitHub Actions for continuous integration and deployment. The CI/CD pipeline includes:

### Workflows

#### 1. Continuous Integration (CI) - `.github/workflows/ci.yml`
- **Triggers**: Push to `main`/`develop` branches, Pull Requests to `main`/`develop`
- **Features**:
  - Automated builds using Gradle
  - Unit test execution
  - Code linting
  - Artifact generation (debug APK)
  - Test result reporting

#### 2. Release Automation - `.github/workflows/release.yml`
- **Triggers**: Git tags following semantic versioning (`v*.*.*`)
- **Features**:
  - Release APK and AAB generation
  - Automated GitHub releases
  - Artifact uploads to releases

#### 3. Security Scanning - `.github/workflows/codeql.yml`
- **Triggers**: Push to `main`/`develop`, PRs to `main`, Weekly schedule
- **Features**:
  - CodeQL security analysis
  - Vulnerability detection
  - Security report generation

#### 4. Dependency Management - `.github/workflows/dependency-review.yml`
- **Triggers**: Pull Requests
- **Features**:
  - Dependency vulnerability scanning
  - License compliance checking
  - Security advisory notifications

#### 5. Automated Dependency Updates - `.github/dependabot.yml`
- **Features**:
  - Weekly dependency updates for Gradle packages
  - GitHub Actions workflow updates
  - Automatic PR creation for updates

### Setup Requirements

1. **Android SDK**: The workflows automatically set up the Android SDK
2. **Java 17**: Required for building the application
3. **Gradle Wrapper**: Included in the project

### Local Development

```bash
# Build the project
./gradlew build

# Run unit tests
./gradlew test

# Run lint checks
./gradlew lint

# Build debug APK
./gradlew assembleDebug

# Build release APK (unsigned)
./gradlew assembleRelease
```

### Release Process

1. Create a new tag with semantic versioning:
   ```bash
   git tag v1.0.0
   git push origin v1.0.0
   ```

2. The release workflow will automatically:
   - Build release APK and AAB
   - Create a GitHub release
   - Upload artifacts to the release

### Security Features

- **CodeQL Analysis**: Automated security vulnerability scanning
- **Dependency Review**: Checks for known vulnerabilities in dependencies
- **Dependabot**: Automated dependency updates with security patches

### Build Artifacts

- **Debug APK**: Available in CI workflow artifacts
- **Release APK/AAB**: Available in GitHub releases
- **Test Reports**: Available in CI workflow artifacts
- **Lint Reports**: Available in CI workflow artifacts

### Caching

The workflows use GitHub Actions caching to speed up builds:
- Gradle dependencies cache
- Gradle wrapper cache
- Android build cache

This reduces build times significantly for subsequent runs.