aws ecr get-login-password --region us-east-1 | docker login --username AWS --password-stdin 913524932573.dkr.ecr.us-east-1.amazonaws.com
aws ecr create-repository --repository-name auth-lambda-repo

docker buildx build --platform linux/amd64 --provenance=false -t docker-image:test .
docker tag docker-image:test  913524932573.dkr.ecr.us-east-1.amazonaws.com/auth-lambda-repo:latest
docker tag docker-image:test  913524932573.dkr.ecr.us-east-1.amazonaws.com/auth-lambda-repo:${EXECUTION__DATE}
docker push 913524932573.dkr.ecr.us-east-1.amazonaws.com/auth-lambda-repo:${EXECUTION__DATE}
docker push 913524932573.dkr.ecr.us-east-1.amazonaws.com/auth-lambda-repo:latest
