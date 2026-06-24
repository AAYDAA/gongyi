declare module 'aos' {
  interface AosOptions {
    duration?: number
    easing?: string
    once?: boolean
  }

  const AOS: {
    init: (options?: AosOptions) => void
    refresh: () => void
    refreshHard: () => void
  }

  export default AOS
}
