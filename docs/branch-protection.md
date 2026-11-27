# Branch Protection Rules

TutorBridge enforces branch protection on both `main` and `develop` so all changes go through reviewed pull requests and automated checks.

## Protected Branches
- `main` — release-only branch; receives PRs from `develop` and triggers production deployment.
- `develop` — default integration branch; receives daily feature/fix/chore PRs from all team members.

## Required GitHub Settings (`main` and `develop`)
1. **Require a pull request before merging**
   - Required approvals: **1**
   - Dismiss stale pull request approvals when new commits are pushed: **Enabled**
   - Require review from Code Owners: **Recommended**
2. **Require status checks to pass before merging**
   - Require branches to be up to date before merging: **Enabled**
   - Required check (configured on Day 3): `CI / Build, test and coverage`
3. **Do not allow bypassing the above settings**
   - Applied to administrators as well (no direct pushes to `main` or `develop`).
4. **Restrict force pushes and branch deletions**
   - Allow force pushes: **Disabled**
   - Allow deletions: **Disabled**

## Screenshots
<!-- Add screenshots of the GitHub Settings -> Branches protection rules for `main` and `develop` here. -->
