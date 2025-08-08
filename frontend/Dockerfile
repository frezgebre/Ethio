# syntax=docker/dockerfile:1
FROM node:22-alpine AS base
WORKDIR /app

COPY package.json ./
# If you later add a lockfile, copy it here for better caching
RUN npm install --silent --no-progress

COPY . .
ENV HOST=0.0.0.0
ENV PORT=5173
EXPOSE 5173

CMD ["npm", "run", "dev", "--", "--host", "0.0.0.0", "--port", "5173"]