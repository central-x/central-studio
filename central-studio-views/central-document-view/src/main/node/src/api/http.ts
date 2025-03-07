import axios from 'axios'

const client = axios.create({
  timeout: 10000,
  headers: {
    Accept: 'application/json',
  },
})

export default client
